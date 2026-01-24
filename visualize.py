#!/usr/bin/env python3
"""
Visualize Java version distribution from check-language-version JSON output.
"""

import os
import sys
import subprocess
import json
from pathlib import Path

# Check if we're running in a venv and have required dependencies
def check_and_setup_venv():
    """Check if running in venv with dependencies, if not create and restart."""
    venv_dir = Path(__file__).parent / ".venv"
    venv_python = venv_dir / "bin" / "python"

    # If we're not in a venv or venv doesn't exist, create it
    if not hasattr(sys, 'real_prefix') and not (hasattr(sys, 'base_prefix') and sys.base_prefix != sys.prefix):
        if not venv_dir.exists():
            print("Creating virtual environment...")
            subprocess.check_call([sys.executable, "-m", "venv", str(venv_dir)])
            print("Installing dependencies...")
            subprocess.check_call([
                str(venv_python), "-m", "pip", "install", "--upgrade", "pip"
            ])
            subprocess.check_call([
                str(venv_python), "-m", "pip", "install",
                "plotly", "click", "kaleido"
            ])

        # Restart script with venv Python
        print("Restarting with virtual environment...")
        os.execv(str(venv_python), [str(venv_python)] + sys.argv)

    # Try importing dependencies, install if missing
    try:
        import plotly
        import click
    except ImportError:
        print("Installing missing dependencies...")
        subprocess.check_call([
            sys.executable, "-m", "pip", "install",
            "plotly", "click", "kaleido"
        ])
        # Restart to reload modules
        os.execv(sys.executable, [sys.executable] + sys.argv)

# Run setup before imports
check_and_setup_venv()

import click
import plotly.graph_objects as go
from plotly.subplots import make_subplots
import plotly.io as pio

# LTS versions in order
LTS_VERSIONS = [8, 11, 17, 21, 25]

def map_to_lts(version):
    """Map a Java version to its corresponding LTS version."""
    if isinstance(version, str) and version.lower() == 'unknown':
        return 'Unknown'

    try:
        ver = int(version)
    except (ValueError, TypeError):
        return 'Unknown'

    # Find the appropriate LTS version
    for lts in LTS_VERSIONS:
        if ver <= lts:
            return lts

    # If higher than all known LTS, return the highest LTS
    return LTS_VERSIONS[-1]

def load_json_data(filepath):
    """Load JSON data from file."""
    with open(filepath, 'r') as f:
        return json.load(f)

def extract_version_stats(data, use_lts=False):
    """Extract statistics from JSON data."""
    files = data.get('files', {})

    version_stats = {}
    for filename, info in files.items():
        java_version = info.get('java', 'Unknown')
        lines = info.get('lines', 0)

        # Map to LTS if requested
        if use_lts:
            java_version = map_to_lts(java_version)

        if java_version not in version_stats:
            version_stats[java_version] = {'files': 0, 'lines': 0}

        version_stats[java_version]['files'] += 1
        version_stats[java_version]['lines'] += lines

    return version_stats

def combine_stats(stats_list):
    """Combine multiple statistics dictionaries."""
    combined = {}
    for stats in stats_list:
        for version, counts in stats.items():
            if version not in combined:
                combined[version] = {'files': 0, 'lines': 0}
            combined[version]['files'] += counts['files']
            combined[version]['lines'] += counts['lines']
    return combined

def create_pie_charts(stats_dict, label):
    """Create pie charts for file count and line count distribution."""
    versions = sorted(stats_dict.keys())
    file_counts = [stats_dict[v]['files'] for v in versions]
    line_counts = [stats_dict[v]['lines'] for v in versions]

    # Create labels with Java version numbers
    labels = [f'Java {v}' if isinstance(v, int) else str(v) for v in versions]

    # Create subplots
    fig = make_subplots(
        rows=1, cols=2,
        subplot_titles=(f'{label} - Files by Java Version',
                       f'{label} - Lines by Java Version'),
        specs=[[{'type': 'pie'}, {'type': 'pie'}]]
    )

    # File count pie chart
    fig.add_trace(
        go.Pie(labels=labels, values=file_counts, name='Files',
               textinfo='label+percent+value',
               hovertemplate='<b>%{label}</b><br>Files: %{value}<br>Percentage: %{percent}<extra></extra>'),
        row=1, col=1
    )

    # Line count pie chart
    fig.add_trace(
        go.Pie(labels=labels, values=line_counts, name='Lines',
               textinfo='label+percent+value',
               hovertemplate='<b>%{label}</b><br>Lines: %{value}<br>Percentage: %{percent}<extra></extra>'),
        row=1, col=2
    )

    fig.update_layout(
        title_text=f'{label} - Java Version Distribution',
        height=500,
        showlegend=True
    )

    return fig

def create_bar_charts(datasets, combined=False):
    """Create bar charts comparing file and line counts across datasets.
    Returns tuple of (files_fig, lines_fig)."""
    if combined:
        # Combine all datasets
        combined_stats = combine_stats([stats for _, stats in datasets])
        datasets = [('Combined', combined_stats)]

    # Collect all unique versions
    all_versions = set()
    for _, stats in datasets:
        all_versions.update(stats.keys())
    versions = sorted(list(all_versions))

    # Create labels
    labels = [f'Java {v}' if isinstance(v, int) else str(v) for v in versions]

    # Use Plotly's default color sequence
    default_colors = pio.templates[pio.templates.default].layout.colorway

    # Create Files figure
    fig_files = go.Figure()

    # Create Lines figure
    fig_lines = go.Figure()

    for idx, (label, stats) in enumerate(datasets):
        file_counts = [stats.get(v, {'files': 0})['files'] for v in versions]
        line_counts = [stats.get(v, {'lines': 0})['lines'] for v in versions]

        # Use the same color for both file and line charts for this dataset
        color = default_colors[idx % len(default_colors)]

        # File count bar chart
        fig_files.add_trace(
            go.Bar(x=labels, y=file_counts, name=label,
                   marker_color=color,
                   hovertemplate='<b>%{x}</b><br>' + f'{label}<br>Files: %{{y}}<extra></extra>')
        )

        # Line count bar chart
        fig_lines.add_trace(
            go.Bar(x=labels, y=line_counts, name=label,
                   marker_color=color,
                   hovertemplate='<b>%{x}</b><br>' + f'{label}<br>Lines: %{{y}}<extra></extra>')
        )

    # Update files figure layout
    fig_files.update_layout(
        height=500,
        showlegend=True,
        barmode='group',
        title_text='Files per Java Version' + (' (Combined)' if combined else ''),
        xaxis_title='Java Version',
        yaxis_title='Number of Files'
    )

    # Update lines figure layout
    fig_lines.update_layout(
        height=500,
        showlegend=True,
        barmode='group',
        title_text='Lines per Java Version' + (' (Combined)' if combined else ''),
        xaxis_title='Java Version',
        yaxis_title='Number of Lines'
    )

    return fig_files, fig_lines

def create_percentage_bar_charts(datasets, combined=False):
    """Create percentage bar charts showing relative distribution across datasets.
    Returns tuple of (files_fig, lines_fig)."""
    if combined:
        # Combine all datasets
        combined_stats = combine_stats([stats for _, stats in datasets])
        datasets = [('Combined', combined_stats)]

    # Collect all unique versions
    all_versions = set()
    for _, stats in datasets:
        all_versions.update(stats.keys())
    versions = sorted(list(all_versions))

    # Create labels
    labels = [f'Java {v}' if isinstance(v, int) else str(v) for v in versions]

    # Use Plotly's default color sequence
    default_colors = pio.templates[pio.templates.default].layout.colorway

    # Create Files percentage figure
    fig_files = go.Figure()

    # Create Lines percentage figure
    fig_lines = go.Figure()

    for idx, (label, stats) in enumerate(datasets):
        file_counts = [stats.get(v, {'files': 0})['files'] for v in versions]
        line_counts = [stats.get(v, {'lines': 0})['lines'] for v in versions]

        # Calculate percentages
        total_files = sum(file_counts)
        total_lines = sum(line_counts)

        file_percentages = [(count / total_files * 100) if total_files > 0 else 0 for count in file_counts]
        line_percentages = [(count / total_lines * 100) if total_lines > 0 else 0 for count in line_counts]

        # Use the same color for both file and line charts for this dataset
        color = default_colors[idx % len(default_colors)]

        # File percentage bar chart
        fig_files.add_trace(
            go.Bar(x=labels, y=file_percentages, name=label,
                   marker_color=color,
                   hovertemplate='<b>%{x}</b><br>' + f'{label}<br>Percentage: %{{y:.1f}}%<extra></extra>')
        )

        # Line percentage bar chart
        fig_lines.add_trace(
            go.Bar(x=labels, y=line_percentages, name=label,
                   marker_color=color,
                   hovertemplate='<b>%{x}</b><br>' + f'{label}<br>Percentage: %{{y:.1f}}%<extra></extra>')
        )

    # Update files percentage figure layout
    fig_files.update_layout(
        height=500,
        showlegend=True,
        barmode='group',
        title_text='Files per Java Version (%)' + (' (Combined)' if combined else ''),
        xaxis_title='Java Version',
        yaxis_title='Percentage of Files',
        yaxis_range=[0, 100]
    )

    # Update lines percentage figure layout
    fig_lines.update_layout(
        height=500,
        showlegend=True,
        barmode='group',
        title_text='Lines per Java Version (%)' + (' (Combined)' if combined else ''),
        xaxis_title='Java Version',
        yaxis_title='Percentage of Lines',
        yaxis_range=[0, 100]
    )

    return fig_files, fig_lines

@click.command()
@click.argument('json_files', nargs=-1, type=click.Path(exists=True), required=True)
@click.option('--combined', is_flag=True, help='Combine all JSON files into one visualization')
@click.option('--open', 'open_browser', is_flag=True, help='Open the HTML index file in browser after generation')
@click.option('--output-dir', default='plots', help='Directory to save output files')
@click.option('--format', 'output_format',
              type=click.Choice(['png', 'html', 'both'], case_sensitive=False),
              default='both', help='Output format for plots')
def main(json_files, combined, open_browser, output_dir, output_format):
    """
    Visualize Java version distribution from check-language-version JSON output.

    JSON_FILES: One or more JSON output files from check-language-version tool.
    The filename (without extension) will be used as the label.
    """
    # Create output directory
    output_path = Path(output_dir)
    output_path.mkdir(exist_ok=True)

    # Create LTS subdirectory
    lts_output_path = output_path / 'lts'
    lts_output_path.mkdir(exist_ok=True)

    # Load and process data for exact versions
    datasets = []
    datasets_lts = []

    for json_file in json_files:
        filepath = Path(json_file)
        label = filepath.stem  # Use filename without extension as label

        click.echo(f'Loading {filepath}...')
        data = load_json_data(filepath)

        # Extract stats for exact versions
        stats = extract_version_stats(data, use_lts=False)
        datasets.append((label, stats))

        # Extract stats for LTS versions
        stats_lts = extract_version_stats(data, use_lts=True)
        datasets_lts.append((label, stats_lts))

        click.echo(f'  Found {sum(s["files"] for s in stats.values())} files '
                  f'across {len(stats)} Java versions')
        click.echo(f'  LTS grouping: {len(stats_lts)} LTS versions')

    # Generate visualizations
    all_figures = []
    all_figures_lts = []

    if combined:
        click.echo('\nGenerating combined visualizations...')

        # Combined pie chart (exact versions)
        combined_stats = combine_stats([stats for _, stats in datasets])
        pie_fig = create_pie_charts(combined_stats, 'Combined')
        all_figures.append(('combined_pie', pie_fig))

        # Combined bar charts (exact versions)
        bar_fig_files, bar_fig_lines = create_bar_charts(datasets, combined=True)
        all_figures.append(('combined_bars_files', bar_fig_files))
        all_figures.append(('combined_bars_lines', bar_fig_lines))

        pct_bar_fig_files, pct_bar_fig_lines = create_percentage_bar_charts(datasets, combined=True)
        all_figures.append(('combined_bars_percentage_files', pct_bar_fig_files))
        all_figures.append(('combined_bars_percentage_lines', pct_bar_fig_lines))

        # Combined LTS visualizations
        click.echo('Generating LTS visualizations...')
        combined_stats_lts = combine_stats([stats for _, stats in datasets_lts])
        pie_fig_lts = create_pie_charts(combined_stats_lts, 'Combined (LTS)')
        all_figures_lts.append(('combined_pie', pie_fig_lts))

        bar_fig_lts_files, bar_fig_lts_lines = create_bar_charts(datasets_lts, combined=True)
        all_figures_lts.append(('combined_bars_files', bar_fig_lts_files))
        all_figures_lts.append(('combined_bars_lines', bar_fig_lts_lines))

        pct_bar_fig_lts_files, pct_bar_fig_lts_lines = create_percentage_bar_charts(datasets_lts, combined=True)
        all_figures_lts.append(('combined_bars_percentage_files', pct_bar_fig_lts_files))
        all_figures_lts.append(('combined_bars_percentage_lines', pct_bar_fig_lts_lines))
    else:
        # Individual pie charts for each dataset (exact versions)
        for label, stats in datasets:
            click.echo(f'\nGenerating pie charts for {label}...')
            pie_fig = create_pie_charts(stats, label)
            all_figures.append((f'{label}_pie', pie_fig))

        # Comparison bar charts (exact versions)
        if len(datasets) > 1:
            click.echo('\nGenerating comparison bar charts...')
            bar_fig_files, bar_fig_lines = create_bar_charts(datasets, combined=False)
            all_figures.append(('comparison_bars_files', bar_fig_files))
            all_figures.append(('comparison_bars_lines', bar_fig_lines))

            pct_bar_fig_files, pct_bar_fig_lines = create_percentage_bar_charts(datasets, combined=False)
            all_figures.append(('comparison_bars_percentage_files', pct_bar_fig_files))
            all_figures.append(('comparison_bars_percentage_lines', pct_bar_fig_lines))
        else:
            click.echo('\nGenerating bar charts...')
            bar_fig_files, bar_fig_lines = create_bar_charts(datasets, combined=False)
            all_figures.append((f'{datasets[0][0]}_bars_files', bar_fig_files))
            all_figures.append((f'{datasets[0][0]}_bars_lines', bar_fig_lines))

            pct_bar_fig_files, pct_bar_fig_lines = create_percentage_bar_charts(datasets, combined=False)
            all_figures.append((f'{datasets[0][0]}_bars_percentage_files', pct_bar_fig_files))
            all_figures.append((f'{datasets[0][0]}_bars_percentage_lines', pct_bar_fig_lines))

        # LTS visualizations
        click.echo('\nGenerating LTS visualizations...')

        # Individual LTS pie charts
        for label, stats in datasets_lts:
            pie_fig_lts = create_pie_charts(stats, f'{label} (LTS)')
            all_figures_lts.append((f'{label}_pie', pie_fig_lts))

        # LTS comparison bar charts
        if len(datasets_lts) > 1:
            bar_fig_lts_files, bar_fig_lts_lines = create_bar_charts(datasets_lts, combined=False)
            all_figures_lts.append(('comparison_bars_files', bar_fig_lts_files))
            all_figures_lts.append(('comparison_bars_lines', bar_fig_lts_lines))

            pct_bar_fig_lts_files, pct_bar_fig_lts_lines = create_percentage_bar_charts(datasets_lts, combined=False)
            all_figures_lts.append(('comparison_bars_percentage_files', pct_bar_fig_lts_files))
            all_figures_lts.append(('comparison_bars_percentage_lines', pct_bar_fig_lts_lines))
        else:
            bar_fig_lts_files, bar_fig_lts_lines = create_bar_charts(datasets_lts, combined=False)
            all_figures_lts.append((f'{datasets_lts[0][0]}_bars_files', bar_fig_lts_files))
            all_figures_lts.append((f'{datasets_lts[0][0]}_bars_lines', bar_fig_lts_lines))

            pct_bar_fig_lts_files, pct_bar_fig_lts_lines = create_percentage_bar_charts(datasets_lts, combined=False)
            all_figures_lts.append((f'{datasets_lts[0][0]}_bars_percentage_files', pct_bar_fig_lts_files))
            all_figures_lts.append((f'{datasets_lts[0][0]}_bars_percentage_lines', pct_bar_fig_lts_lines))

    # Save figures (exact versions)
    for name, fig in all_figures:
        if output_format in ['png', 'both']:
            png_path = output_path / f'{name}.png'
            click.echo(f'Saving {png_path}...')
            try:
                fig.write_image(str(png_path), width=1400, height=fig.layout.height or 600)
            except Exception as e:
                click.echo(f'Warning: Could not save PNG (is kaleido installed?): {e}', err=True)

        if output_format in ['html', 'both']:
            html_path = output_path / f'{name}.html'
            click.echo(f'Saving {html_path}...')
            fig.write_html(str(html_path))

    # Save LTS figures
    click.echo('\nSaving LTS figures...')
    for name, fig in all_figures_lts:
        if output_format in ['png', 'both']:
            png_path = lts_output_path / f'{name}.png'
            click.echo(f'Saving {png_path}...')
            try:
                fig.write_image(str(png_path), width=1400, height=fig.layout.height or 600)
            except Exception as e:
                click.echo(f'Warning: Could not save PNG (is kaleido installed?): {e}', err=True)

        if output_format in ['html', 'both']:
            html_path = lts_output_path / f'{name}.html'
            click.echo(f'Saving {html_path}...')
            fig.write_html(str(html_path))

    # Create an index HTML file combining all plots
    if output_format in ['html', 'both'] and (len(all_figures) > 1 or len(all_figures_lts) > 0):
        index_path = output_path / 'index.html'
        click.echo(f'\nCreating index page at {index_path}...')

        html_content = ['<!DOCTYPE html>', '<html>', '<head>',
                       '<meta charset="UTF-8">',
                       '<title>Java Version Analysis</title>',
                       '<style>',
                       'body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }',
                       'h1 { color: #333; }',
                       'h2.section { color: #555; margin-top: 40px; padding-top: 20px; border-top: 2px solid #ddd; }',
                       '.plot { background: white; margin: 20px 0; padding: 10px; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }',
                       'iframe { border: none; }',
                       '.description { color: #666; margin-bottom: 20px; font-style: italic; }',
                       '</style>',
                       '</head>', '<body>',
                       '<h1>Java Version Analysis</h1>']

        # Exact versions section
        if all_figures:
            html_content.append('<h2 class="section">Exact Java Versions</h2>')
            html_content.append('<p class="description">Showing actual Java version numbers found in the codebase.</p>')
            for name, _ in all_figures:
                html_filename = f'{name}.html'
                html_content.append(f'<div class="plot">')
                html_content.append(f'<h2>{name.replace("_", " ").title()}</h2>')
                html_content.append(f'<iframe src="{html_filename}" width="100%" height="650"></iframe>')
                html_content.append('</div>')

        # LTS versions section
        if all_figures_lts:
            html_content.append('<h2 class="section">LTS Java Versions</h2>')
            html_content.append('<p class="description">Versions mapped to their corresponding LTS releases (8, 11, 17, 21, 25).</p>')
            for name, _ in all_figures_lts:
                html_filename = f'lts/{name}.html'
                html_content.append(f'<div class="plot">')
                html_content.append(f'<h2>{name.replace("_", " ").title()} (LTS)</h2>')
                html_content.append(f'<iframe src="{html_filename}" width="100%" height="650"></iframe>')
                html_content.append('</div>')

        html_content.extend(['</body>', '</html>'])

        with open(index_path, 'w') as f:
            f.write('\n'.join(html_content))

    # Get absolute path for final message
    abs_output_path = output_path.resolve()
    click.echo(f'\n✓ Done! Output saved to {abs_output_path}/')
    if output_format in ['html', 'both']:
        index_file = abs_output_path / 'index.html'
        click.echo(f'  Open {index_file} to view interactive plots')

        # Open browser if requested
        if open_browser:
            import webbrowser
            click.echo(f'  Opening {index_file} in browser...')
            webbrowser.open(f'file://{index_file}')

if __name__ == '__main__':
    main()