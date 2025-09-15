package ir;
import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    private List<IRInstruction> instructions;
    private List<BasicBlock> predecessors;
    private List<BasicBlock> successors;
    private String label = "";
    private int blockId;

    public BasicBlock(int blockId) {
        this.instructions = new ArrayList<>();
        this.predecessors = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.blockId = blockId;
    }

    public List<IRInstruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<IRInstruction> instructions) {
        this.instructions = instructions;
    }

    public List<BasicBlock> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<BasicBlock> predecessors) {
        this.predecessors = predecessors;
    }

    public List<BasicBlock> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<BasicBlock> successors) {
        this.successors = successors;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
}
