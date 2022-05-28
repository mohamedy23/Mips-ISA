import java.util.Hashtable;

public class Fetch extends Stage {

	public Fetch() {
		super();
	}

	@Override
	public void setClock(int clock) {

		if (clock % 2 != 0 && active) {
			canRun = true;
			swap();
			active = false;
			System.out.println("Fetch instruction " + ((int) (currentData.get("PC") + 1)) + " " + currentData);
            System.out.println("Data to Stage");
			System.out.println("PC " +  currentData.get("PC"));
			System.out.println("*********************************");
		}

	}

	@Override
	public void run(Stage nextStage) {
		if (canRun) {
			canRun = false;
			Integer index = currentData.get("PC");
			Integer Instruction = Arc.getMem(index);
			Hashtable<String, Integer> nextDecode = nextStage.nextData;
			nextDecode.put("Instruction", Instruction);
			nextDecode.put("PC", index);
			Arc.setPC(index + 1);
			nextStage.active = true;
			if (index + 1 >= 1024 || Arc.getMem(index + 1) == null) {
				this.active = false;
			} else {
				this.nextData.put("PC", index + 1);
				this.active = true;
			}
			Arc.setPC(index + 1);

		}

	}

}
