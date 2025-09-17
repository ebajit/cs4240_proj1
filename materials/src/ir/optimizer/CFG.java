package ir.optimizer;

import ir.BasicBlock;
import ir.IRFunction;
import ir.IRInstruction;
import ir.IRInstruction.OpCode;
import ir.operand.IRLabelOperand;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CFG {
   private List<BasicBlock> basicBlockList;
   private Map<String, BasicBlock> labelToBlockMap;
   private IRFunction parentFunc;

   /**
    * Constructs a control flow graph from the given IR function.
    * Transforms linear instruction sequence into connected basic blocks.
    * 
    * @param function the IR function to convert into a CFG
    */
   public CFG(IRFunction function) {
      this.parentFunc = function;
      this.labelToBlockMap = new HashMap<>();
      this.basicBlockList = new ArrayList<>();
      createCFG(function);
   }

   /**
    * Builds the control flow graph by partitioning instructions into basic blocks
    * and establishing control flow connections between them.
    * 
    * @param function the source IR function
    */
   private void createCFG(IRFunction function) {
      int blockCounter = 0;
      BasicBlock currBlock = new BasicBlock(blockCounter);
      currBlock.setLabel("start");
      labelToBlockMap.put(currBlock.getLabel(), currBlock);
      for (IRInstruction currInstr: function.instructions) {
         OpCode currOp = currInstr.opCode;
         if (currOp == OpCode.LABEL) {
            if (!currBlock.isEmpty()) {
               basicBlockList.add(currBlock);
               blockCounter++;
               currBlock = new BasicBlock(blockCounter);
            }
            currBlock.setLabel(currInstr.operands[0].toString());
            labelToBlockMap.put(currBlock.getLabel(), currBlock);
         }
         currBlock.add(currInstr);
         if (controlFlowCheck(currInstr)) {
            basicBlockList.add(currBlock);
            blockCounter++;
            currBlock = new BasicBlock(blockCounter);
         }
      }
      if (!currBlock.isEmpty()) {
         basicBlockList.add(currBlock);
      }
      createBlockLinks();
   }

   /**
    * Creates predecessor and successor relationships between basic blocks
    * based on control flow instructions and fall-through behavior.
    */
   private void createBlockLinks() {
      for (int blockIndex = 0; blockIndex < basicBlockList.size(); blockIndex++) {
         BasicBlock currBlock = basicBlockList.get(blockIndex);
         IRInstruction lastInstr = currBlock.getLastInstruction();
         OpCode terminalOp = lastInstr.opCode;
         if (terminalOp == OpCode.GOTO) {
            String targetLabel = ((IRLabelOperand) lastInstr.operands[0]).getName();
            currBlock.addSuccessor(labelToBlockMap.get(targetLabel));
            labelToBlockMap.get(targetLabel).addPredecessor(currBlock);
         } else if (branchOpCheck(terminalOp)) {
            String branchLabel = ((IRLabelOperand) lastInstr.operands[0]).getName();
            currBlock.addSuccessor(labelToBlockMap.get(branchLabel));
            labelToBlockMap.get(branchLabel).addPredecessor(currBlock);
            if (blockIndex + 1 < basicBlockList.size()) {
               currBlock.addSuccessor(basicBlockList.get(blockIndex + 1));
               basicBlockList.get(blockIndex + 1).addPredecessor(currBlock);
            }
         } else if (terminalOp != OpCode.RETURN && blockIndex + 1 < basicBlockList.size()) {
            currBlock.addSuccessor(basicBlockList.get(blockIndex + 1));
            basicBlockList.get(blockIndex + 1).addPredecessor(currBlock);
         }
      }
   }

   /**
    * Determines if an instruction terminates a basic block by transferring control.
    * 
    * @param instr the instruction to examine
    * @return true if the instruction ends a basic block
    */
   private boolean controlFlowCheck(IRInstruction instr) {
      OpCode instrCode = instr.opCode;
      return instrCode == OpCode.BREQ || instrCode == OpCode.BRNEQ || instrCode == OpCode.BRLT || instrCode == OpCode.BRGT || instrCode == OpCode.BRGEQ || instrCode == OpCode.GOTO;
   }

   /**
    * Checks if an operation code represents a conditional branch instruction.
    * 
    * @param opCode the operation code to check
    * @return true if the operation is a conditional branch
    */
   private boolean branchOpCheck(OpCode opCode) {
      return opCode == OpCode.BREQ || opCode == OpCode.BRNEQ || opCode == OpCode.BRLT || opCode == OpCode.BRGT || opCode == OpCode.BRGEQ; 
   }

   /**
    * Reconstructs an IR function from the optimized control flow graph.
    * Flattens basic blocks back into a linear instruction sequence.
    * 
    * @return new IR function with optimized instruction sequence
    */
   public IRFunction CFGToIRFunction() {
      List<IRInstruction> reconstructedInstrs = new ArrayList<>();
      List<IRVariableOperand> collectedVars = new ArrayList<>();
      Set<IRVariableOperand> uniqueVarSet = new HashSet<>();
      IRFunction resultFunc = new IRFunction(parentFunc.name, parentFunc.returnType, parentFunc.parameters, parentFunc.variables, reconstructedInstrs);
      for (BasicBlock currBlock : basicBlockList) {
         for (IRInstruction currInstr : currBlock.getInstructions()) {
            reconstructedInstrs.add(currInstr);
            for (IROperand currOperand : currInstr.operands)
               if (currOperand instanceof IRVariableOperand) {
                  if (!uniqueVarSet.contains(currOperand)) {
                     uniqueVarSet.add((IRVariableOperand) currOperand);
                     collectedVars.add((IRVariableOperand) currOperand);
                  }
               }   
         }
      }
      return resultFunc;
   }

   /**
    * Returns the list of basic blocks in this control flow graph.
    * 
    * @return list of basic blocks
    */
   public List<BasicBlock> getBlocks() {
      return this.basicBlockList;
   }
}
