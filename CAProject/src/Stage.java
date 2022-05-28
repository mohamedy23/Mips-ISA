import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public abstract class Stage {

	boolean active;
	boolean canRun;
	boolean finish;
	protected Hashtable<String, Integer> currentControl;
	protected Hashtable<String, Integer> nextControl;
	protected Hashtable<String, Integer> currentData;
	protected Hashtable<String, Integer> nextData;
	protected boolean insRunning = false;

	public Stage() {
		this.active = false;
		this.canRun = false;
		this.finish = false;
		currentControl = new Hashtable<String, Integer>();
		nextControl = new Hashtable<String, Integer>();
		currentData = new Hashtable<String, Integer>();
		nextData = new Hashtable<String, Integer>();
		int clock = 0;
	}

	public Hashtable<String, Integer> getNextControl() {
		return nextControl;
	}

	public Hashtable<String, Integer> getNextData() {
		return nextData;
	}

	public void swap() {
		Hashtable<String, Integer> temp = currentControl;
		currentControl = nextControl;
		nextControl = temp;
		temp = currentData;
		currentData = nextData;
		nextData = temp;

	}

	public void printDate() {
		Set<String> data = currentData.keySet();
		Set<String> control = currentControl.keySet();
		System.out.println("Data to Stage ");
		for(String s:data) {
			if(!s.equals("PC"))
			System.out.print(s+"  "+currentData.get(s)+",  ");
		}
		System.out.println();
		System.out.println("Control Signal to statge ");
		for(String s:control) {
			System.out.print(s+"  "+currentControl.get(s)+",  ");
		}
		System.out.println();
		System.out.println("****************************************");
	}

	public abstract void setClock(int clock);

	public abstract void run(Stage nextStage);

}
