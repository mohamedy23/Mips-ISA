import java.util.Hashtable;

public class Memory extends Stage {

	@Override
	public void setClock(int clock) {

		if (clock % 2 == 0 && active) {
			canRun = true;
			swap();
			active = false;
			System.out.println("Memory instruction " +  ((int)(currentData.get("PC") +1)));
			 this.printDate();
		}

	}

	@Override
	public void run(Stage nextStage) {
		if (canRun) {
			canRun = false;
			int memWrite = currentControl.get("MemWrite");
			int memRead = currentControl.get("MemRead");
			int address = currentData.get("Result");
			int writeData = currentData.get("WritDate");
			Hashtable<String, Integer> nextDataS = nextStage.nextData;
			Integer readData = -1;
			if (memRead == 1) {
				readData = Arc.getMem(address);
			} else if (memWrite == 1) {
				Arc.setMem(address, writeData);
			}
			nextDataS.put("Result", address);
			nextDataS.put("ReadData", readData);
			nextDataS.put("WritReg", currentData.get("WriteReg"));
			setNextControlSignal(nextStage.nextControl);
			nextDataS.put("PC", currentData.get("PC"));
			nextStage.active = true;
		}
	}

	private void setNextControlSignal(Hashtable<String, Integer> newControl) {
		newControl.put("RegWrite", currentControl.get("RegWrite"));
		newControl.put("MemtoReg", currentControl.get("MemtoReg"));

	}

}
