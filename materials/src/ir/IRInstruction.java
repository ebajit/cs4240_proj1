package ir;

import ir.operand.IRConstantOperand;
import ir.operand.IRFunctionOperand;
import ir.operand.IRLabelOperand;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

public class IRInstruction {

    public enum OpCode {
        ASSIGN,
        ADD, SUB, MULT, DIV, AND, OR,
        GOTO,
        BREQ, BRNEQ, BRLT, BRGT, BRGEQ,
        RETURN,
        CALL, CALLR,
        ARRAY_STORE, ARRAY_LOAD,
        LABEL;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public OpCode opCode;

    public IROperand[] operands;

    public int irLineNumber;

    public boolean marked; // Needed to keep track of instructions in dead code elimination

    public IRInstruction() {}

    public IRInstruction(OpCode opCode, IROperand[] operands, int irLineNumber) {
        this.opCode = opCode;
        this.operands = operands;
        this.irLineNumber = irLineNumber;
        this.marked = false;
    }

    // Get the assigned variable
    public IRVariableOperand getAssignedVariable() {
        if (operands.length > 0 && operands[0] instanceof IRVariableOperand) {
            return (IRVariableOperand) operands[0];
        }
        return null;
    }

    // Check if the instruction is definition
    public boolean checkIfDef() {
        return this.opCode == OpCode.ASSIGN || this.opCode == OpCode.ADD || this.opCode == OpCode.SUB || 
               this.opCode == OpCode.MULT || this.opCode == OpCode.DIV || this.opCode == OpCode.AND ||
               this.opCode == OpCode.OR || this.opCode == OpCode.CALLR || this.opCode == OpCode.ARRAY_LOAD;
    }

    // Need to add this since it doesn't already exist in IRInstruction
    public boolean checkIfCriticalInstr() {
        return this.opCode == OpCode.GOTO || this.opCode == OpCode.BREQ || this.opCode == OpCode.BRNEQ || 
               this.opCode == OpCode.BRLT || this.opCode == OpCode.BRGT || this.opCode == OpCode.BRGEQ ||
               this.opCode == OpCode.RETURN || this.opCode == OpCode.CALLR || this.opCode == OpCode.CALL ||
               this.opCode == OpCode.ARRAY_STORE || this.opCode == OpCode.LABEL;
    }
 }
