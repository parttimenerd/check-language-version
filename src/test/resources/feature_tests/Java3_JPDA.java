// Java 1.3 feature: JPDA (Java Platform Debugger Architecture)
// Expected Version: 3
// Required Features: JPDA
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.Bootstrap;

class Java3_JPDA {
    public boolean doesVirtualMachineHaveNoName() {
        return Bootstrap.virtualMachineManager().defaultConnector().toString().isEmpty();
    }
}