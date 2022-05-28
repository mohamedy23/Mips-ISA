import java.util.Hashtable;

public class Execute extends Stage {

	public Execute() {
		super();
	}

	@Override
	public void setClock(int clock) {

		if (clock % 2 == 0 && active) {
			canRun = true;
			swap();
			active = false;
			System.out.println("Excute instruction " + ((int) (currentData.get("PC") + 1)));
			this.printDate();
			this.insRunning = true;
		} else if (insRunning) {
			System.out.println("Excute instruction " + ((int) (currentData.get("PC") + 1)));
			this.printDate();
			this.insRunning = false;
		}

	}

	@Override
	public void run(Stage nextStage) {
		if (canRun) {
			canRun = false;
			Integer R2 = currentData.get("R2");
			Integer R3 = currentData.get("R3");
			if (currentControl.get("ALUSrc") == 1 && currentControl.get("Shift") == 1)
				R3 = currentData.get("Shamt");
			else if (currentControl.get("ALUSrc") == 1 && currentControl.get("Shift") == 0)
				R3 = currentData.get("Immediate");
			Integer res = AluOp(R2, R3, currentControl.get("AluOp"));
			int zero = 0;
			if (res == 0)
				zero = 1;
			Integer branch = currentData.get("Immediate") + currentData.get("PC") + 1;
			Integer jump = currentData.get("Jump");
			if (currentControl.get("Branch") == 1 && zero == 0)
				Arc.branchAndJump(branch);
			else if (currentControl.get("Jump") == 1)
				Arc.branchAndJump(jump);

			Hashtable<String, Integer> nextDataS = nextStage.nextData;
			nextDataS.put("Result", res);
			nextDataS.put("WriteReg", currentData.get("WriteReg"));
			nextDataS.put("WritDate", currentData.get("R3"));
			nextDataS.put("PC", currentData.get("PC"));
			setNextControlSignal(nextStage.nextControl);
			nextStage.active = true;
		}

	}

	private void setNextControlSignal(Hashtable<String, Integer> newControl) {
		newControl.put("RegWrite", currentControl.get("RegWrite"));
		newControl.put("MemRead", currentControl.get("MemRead"));
		newControl.put("MemWrite", currentControl.get("MemWrite"));
		newControl.put("MemtoReg", currentControl.get("MemtoReg"));

	}

	private Integer AluOp(Integer R2, Integer R3, Integer code) {
		Integer res = 0;
		switch (code) {
		case 0:
			res = R2 + R3;
			break;
		case 1:
			res = R2 - R3;
			break;
		case 2:
			res = R2 * R3;
			break;
		case 3:
			res = R2 + R3;
			break;
		case 4:
			res = R2 - R3;
			break;
		case 5:
			res = R2 & R3;
			break;
		case 6:
			res = R2 | R3;
			break;
		case 7:
			res = R2 + R3;
			break;
		case 8:
			res = R2 << R3;
			break;
		case 9:
			res = R2 >>> R3;
			break;
		case 10:
			res = R2 + R3;
			break;
		case 11:
			res = R2 + R3;
			break;

		}
		return res;
	}

}
