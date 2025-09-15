package ir.optimizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import ir.BasicBlock;
import ir.IRFunction;
import ir.IRInstruction;
import ir.IRInstruction.OpCode;
import ir.operand.IRLabelOperand;

public class CFG {
   private List<BasicBlock> blocks;
   private Map<String, BasicBlock> labelMap;
   private IRFunction parent;

   public CFG(IRFunction function) {
      this.parent = function;
      this.labelMap = new HashMap<>();
      this.blocks = new ArrayList<>();
      createCFG(function);
   }

   /**
    * Iterates through each instruction in the passed in IRFunction object
    * to create basic blocks and linked them to each other.
    * @param function
    */
   private void createCFG(IRFunction function) {
      // initialize start block
      int blockId = 0;
      BasicBlock currBlock = new BasicBlock(blockId);
      currBlock.setLabel("start");
      labelMap.put(currBlock.getLabel(), currBlock);

      // Loop thru each instruction in the IRFunction
      for (IRInstruction instr: function.instructions) {
         OpCode op = instr.opCode;
         if (op == OpCode.LABEL) {
            if (!currBlock.isEmpty()) {
               blocks.add(currBlock);
               blockId++;
               currBlock = new BasicBlock(blockId);
            }
            currBlock.setLabel(instr.operands[0].toString());
            labelMap.put(currBlock.getLabel(), currBlock);
         }
         currBlock.add(instr);
         if (controlFlowCheck(instr)) {
            blocks.add(currBlock);
            blockId++;
            currBlock = new BasicBlock(blockId);
         }
      }
      // Check if curr block empty and add if not empty
      if (!currBlock.isEmpty()) {
         blocks.add(currBlock);
      }

      // Connect blocks to build cfg
      connectBlocks();
   }

   private void connectBlocks() {
      for (int i = 0; i < blocks.size(); i++) {
         BasicBlock currBlock = blocks.get(i);
         IRInstruction lastInstr = currBlock.getLastInstruction();
         OpCode lastOp = lastInstr.opCode;

         if (lastOp == OpCode.GOTO) {
            String label = ((IRLabelOperand) lastInstr.operands[0]).getName();
            currBlock.addSuccessor(labelMap.get(label));
            labelMap.get(label).addPredecessor(currBlock);
         } else if (branchOpCheck(lastOp)) {
            String label = ((IRLabelOperand) lastInstr.operands[0]).getName();
            currBlock.addSuccessor(labelMap.get(label));
            labelMap.get(label).addPredecessor(currBlock);
            if (i + 1 < blocks.size()) {
               currBlock.addSuccessor(blocks.get(i + 1));
               blocks.get(i + 1).addPredecessor(currBlock);
            }
         } else if (lastOp != OpCode.RETURN && i + 1 < blocks.size()) {
            currBlock.addSuccessor(blocks.get(i+1));
            blocks.get(i+1).addPredecessor(currBlock);
         }
      }
   }

   /**
    * Helper method for checking any type of control flow operation
    * @param instr the input instruction
    * @return boolean value, true if instruction is control flow
    */
   private boolean controlFlowCheck(IRInstruction instr) {
      OpCode opcode = instr.opCode;
      return opcode == OpCode.BREQ || opcode == OpCode.BRNEQ || opcode == OpCode.BRLT || opcode == OpCode.BRGT || opcode == OpCode.BRGEQ || opcode == OpCode.GOTO;
   }

   /**
    * Helper method for checking BRANCH only control flow
    * @param op input instruction
    * @return boolean value, true if instruction is branch
    */
   private boolean branchOpCheck(OpCode op) {
      return op == OpCode.BREQ || op == OpCode.BRNEQ || op == OpCode.BRLT || op == OpCode.BRGT || op == OpCode.BRGEQ; 
   }
}
