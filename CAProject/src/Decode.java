import java.util.Hashtable;

public class Decode extends Stage {
	public Decode() {
		super();
	}

	@Override
	public void setClock(int clock) {
		if (clock % 2 == 0 && active) {
			canRun = true;
			swap();
			active = false;
            System.out.println("Decode instruction " +((int)(currentData.get("PC") +1)));
            this.printDate();
            this.insRunning=true;
		}
		else if(insRunning) {
			System.out.println("Decode instruction "+ ((int)(currentData.get("PC") +1)));
			 this.printDate();
			this.insRunning=false;
		}

	}

	@Override
	public void run(Stage nextStage) {
		if (canRun) {
			canRun = false;
			Integer instruction = currentData.get("Instruction");
			Integer PC = currentData.get("PC");
			Integer OpCode = instruction & 0b11110000000000000000000000000000;
			OpCode = OpCode >>> 28;
			Integer R1 = instruction & 0b00001111100000000000000000000000;
			R1 = R1 >>> 23;
			Integer R2 = instruction & 0b00000000011111000000000000000000;
			R2 = R2 >>> 18;
			Integer R3 = instruction & 0b00000000000000111110000000000000;
			R3 = R3 >>> 13;
			Integer Shamt = instruction & 0b00000000000000000001111111111111;
			Integer Immediate = instruction & 0b00000000000000111111111111111111;
			Immediate = Immediate << 14;
			Immediate = Immediate >> 14;
			Integer Jump = instruction & 0b00001111111111111111111111111111;
			Hashtable<String, Integer> nextExecute = nextStage.nextData;
			nextExecute.put("PC", PC);
			controlSignal(OpCode);
			if (Arc.getReg(R2) == null)
				nextExecute.put("R2", 0);
			else
				nextExecute.put("R2", Arc.getReg(R2));
			if (Arc.getReg(R3) == null)
				nextExecute.put("R3", 0);
			else
				nextExecute.put("R3", Arc.getReg(R3));
			if (currentControl.get("RegDst") == 1) {
				if (Arc.getReg(R1) == null)
					nextExecute.put("R3", 0);
				else
					nextExecute.put("R3", Arc.getReg(R1));
			}
			nextExecute.put("WriteReg", R1);
			nextExecute.put("Shamt", Shamt);
			nextExecute.put("Immediate", Immediate);
			nextExecute.put("Jump", Jump);
			setNextControlSignal(nextStage.nextControl);
			nextStage.active=true;

		}

	}

	private void setNextControlSignal(Hashtable<String, Integer> newControl) {
		newControl.put("AluOp", currentControl.get("AluOp"));
		newControl.put("ALUSrc", currentControl.get("ALUSrc"));
		newControl.put("RegWrite", currentControl.get("RegWrite"));
		newControl.put("MemRead", currentControl.get("MemRead"));
		newControl.put("MemWrite", currentControl.get("MemWrite"));
		newControl.put("Branch", currentControl.get("Branch"));
		newControl.put("MemtoReg", currentControl.get("MemtoReg"));
		newControl.put("Jump", currentControl.get("Jump"));
		newControl.put("Shift", currentControl.get("Shift"));
	}

	private Hashtable<String, Integer> controlSignal(Integer OpCode) {
		Hashtable<String, Integer> control = new Hashtable<String, Integer>();
		Integer AluOp = OpCode;
		Integer RegDst = 0;
		Integer ALUSrc = 0;
		Integer RegWrite = 0;
		Integer MemRead = 0;
		Integer MemWrite = 0;
		Integer Branch = 0;
		Integer MemtoReg = 0;
		Integer Jump = 0;
		Integer Shift = 0;
		if (OpCode == 0 || OpCode == 1) {
			RegWrite = 1;
		} else if (OpCode == 8) {
			ALUSrc = 1;
			RegWrite = 1;
			Shift = 1;
		} else if (OpCode == 9) {
			ALUSrc = 1;
			RegWrite = 1;
			Shift = 1;
		} else if (OpCode == 2 || OpCode == 3 || OpCode == 5 || OpCode == 6) {
			ALUSrc = 1;
			RegWrite = 1;
		} else if (OpCode == 4) {
			RegDst = 1;
			Branch = 1;
		} else if (OpCode == 10) {
			ALUSrc = 1;
			RegWrite = 1;
			MemRead = 1;
			MemtoReg = 1;
		} else if (OpCode == 11) {
			RegDst = 1;
			ALUSrc = 1;
			MemWrite = 1;
		} else if (OpCode == 7) {
			Jump = 1;
		}
		Hashtable<String, Integer> newControl = this.currentControl;
		newControl.put("AluOp", AluOp);
		newControl.put("RegDst", RegDst);
		newControl.put("ALUSrc", ALUSrc);
		newControl.put("RegWrite", RegWrite);
		newControl.put("MemRead", MemRead);
		newControl.put("MemWrite", MemWrite);
		newControl.put("Branch", Branch);
		newControl.put("MemtoReg", MemtoReg);
		newControl.put("Jump", Jump);
		newControl.put("Shift", Shift);
		return newControl;

	}

}
