
public class WriteBack extends Stage {
	public WriteBack() {
		super();
	}

	@Override
	public void setClock(int clock) {

		if (clock % 2 != 0 && active) {
			canRun = true;
			swap();
			active = false;
			System.out.println("WriteBack" + " instruction " + ((int) (currentData.get("PC") + 1)));
			this.printDate();
		}

	}

	@Override
	public void run(Stage nextStage) {

		if (canRun) {
			canRun = false;
			Integer writeReg = currentData.get("WritReg");
			Integer writeData = currentData.get("Result");
			if (currentControl.get("MemtoReg") == 1) {
				writeData = currentData.get("ReadData");
			}
			if (currentControl.get("RegWrite") == 1) {
				Arc.setReg(writeReg, writeData);
			}
		}

	}

}
