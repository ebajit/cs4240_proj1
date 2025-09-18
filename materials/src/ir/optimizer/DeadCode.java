package ir.optimizer;

import ir.optimizer.CFG;
import ir.IRInstruction;
import ir.BasicBlock;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;

public class DeadCode {
    private CFG controlFlowGraph;
    private Map<IRInstruction, Set<IRInstruction>> instrDefMap;
    private Queue<IRInstruction> worklist;
    private int removedInstrCount;

    /**
     * Constructs a dead code elimination optimizer for the given control flow graph.
     * Uses reaching definitions analysis to identify and remove unreachable code.
     * 
     * @param cfg the control flow graph to optimize
     * @param defToUseMap mapping from instructions to their reaching definitions
     */
    public DeadCode(CFG cfg, Map<IRInstruction, Set<IRInstruction>> defToUseMap) {
        this.controlFlowGraph = cfg;
        this.instrDefMap = defToUseMap;
        this.worklist = new LinkedList<>();
        this.removedInstrCount = 0;
    }

    /**
     * Executes the complete dead code elimination optimization process.
     * Applies mark-and-sweep algorithm to remove unreachable instructions.
     */
    public void performDCE() {
        mark();
        sweep();
        System.out.println("Dead Code Elimination: Removed " + removedInstrCount + " instructions.");
    }

    /**
     * Marks all essential instructions and instructions reachable through dependency chains.
     * Essential instructions include control flow operations, function calls, and side-effect operations.
     */
    private void mark() {
        markCriticalInstr();
        markWorklistReachingDefs();
    }

    /**
     * Identifies and marks all essential instructions that cannot be eliminated.
     * These include critical operations like branches, calls, and memory operations.
     */
    private void markCriticalInstr() {
        for (BasicBlock currBlock : controlFlowGraph.getBlocks()) {
            for (IRInstruction currentInstruction : currBlock.getInstructions()) {
                if (currentInstruction.checkIfCriticalInstr()) {
                    currentInstruction.marked = true;
                    worklist.add(currentInstruction);
                }
            }
        }
    }

    /**
     * Searches through use-definition chains to mark more instructions in addition to the critical instructions.
     * Processes worklist until no more instructions can be marked as needed.
     */
    private void markWorklistReachingDefs() {
        while (!worklist.isEmpty()) {
            IRInstruction currentInstruction = worklist.poll();
            for (IROperand operand : currentInstruction.operands) {
                if (operand instanceof IRVariableOperand) {
                    String varName = ((IRVariableOperand) operand).getName();
                    Set<IRInstruction> definingInstructions = findReachingDefs(currentInstruction, varName);
                    for (IRInstruction definingInstruction : definingInstructions) {
                        if (!definingInstruction.marked) {
                            definingInstruction.marked = true;
                            worklist.add(definingInstruction);
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes all unmarked instructions from the control flow graph.
     * Updates statistics on the number of eliminated instructions.
     */
    private void sweep() {
        for (BasicBlock currBlock : controlFlowGraph.getBlocks()) {
            int originalSize = currBlock.getInstructions().size();
            currBlock.getInstructions().removeIf(instruction -> !instruction.marked);
            int finalSize = currBlock.getInstructions().size();
            removedInstrCount += (originalSize - finalSize);
        }
    }

    /**
     * Locates all definition instructions that reach a specific variable usage.
     * Filters reaching definitions by variable name to find relevant definitions.
     * 
     * @param instruction the instruction using the variable
     * @param varName the name of the variable being used
     * @return set of instructions that define the specified variable and reach the usage point
     */
    private Set<IRInstruction> findReachingDefs(IRInstruction instruction, String varName) {
        Set<IRInstruction> reachingDef = instrDefMap.get(instruction);
        Set<IRInstruction> relevantDefs = new HashSet<>();
        for (IRInstruction potentialDef : reachingDef) {
            if (potentialDef.checkIfDef() 
                && potentialDef.getAssignedVariable().getName().equals(varName)) {
                relevantDefs.add(potentialDef);
            }
        }
        return relevantDefs;
    }
}

