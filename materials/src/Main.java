import java.io.PrintStream;
import ir.IRReader;
import ir.IRProgram;
import ir.IRFunction;
import ir.IRPrinter;
import ir.optimizer.CFG;
import ir.optimizer.DeadCode;
import ir.optimizer.Reaching;

public class Main {
    public static void main(String[] args) throws Exception {
        IRReader reader = new IRReader();
        IRProgram rawProg = reader.parseIRFile(args[0]);
        IRProgram optimizedProg = new IRProgram();
        for (IRFunction func : rawProg.functions) {
            CFG cfg = new CFG(func);
            Reaching definitions = new Reaching(cfg);
            DeadCode dce = new DeadCode(cfg, definitions.getReachingDefs());
            dce.performDCE();
            optimizedProg.functions.add(cfg.CFGToIRFunction());
        }
        IRPrinter printer = new IRPrinter(new PrintStream(args[1]));
        printer.printProgram(optimizedProg);
    }
}