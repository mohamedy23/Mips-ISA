import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

public class Arc {
	private static Integer[] memory;
	private static Integer[] registerFile;
	private static int pc;
	private int lastInstruction;
	public static int clock;
	private static boolean branchFlag = false;
	private static Integer addressBranch = 0;
	private static Stage[] stages;

	public Arc(String pathFile) throws Exception {
		memory = new Integer[2048];
		registerFile = new Integer[32];
		registerFile[0] = 0;
		pc = 0;
		lastInstruction = 0;
		parseInstructions(pathFile);
		clock = 0;
		stages = new Stage[5];
		stages[0] = new Fetch();
		stages[1] = new Decode();
		stages[2] = new Execute();
		stages[3] = new Memory();
		stages[4] = new WriteBack();
		stages[0].getNextData().put("PC", pc);
		stages[0].active = true;

	}

	public void runArc() {
		while (true) {
			boolean running = false;
			for (Stage s : stages)
				running = running || s.canRun || s.active;
			if (!running) {
				printMemAndReg();
				return;
			}
			clock++;
			System.out.println("At clock " + clock);
			for (int i = 0; i < 5; i++) {
				stages[i].setClock(clock);
			}

			for (int i = 0; i < 5; i++) {
				if (i == 4)
					stages[i].run(null);
				else
					stages[i].run(stages[i + 1]);
			}
			branchOp();
			System.out.println("-----------------------------------------");
		}

		

	}

	public static void setReg(int add, Integer r) {
		if (add == 0) {
			System.out.println("Register " + add + " " + registerFile[add] + " ----->  " + 0);
			return;
		}
		System.out.println("Register " + add + " " + registerFile[add] + " ----->  " + r);
		registerFile[add] = r;
	}

	public static Integer getReg(int add) {
		return registerFile[add];
	}

	public static void setMem(int add, Integer r) {
		System.out.println("Memory " + add + " " + memory[add] + " ----->  " + r);
		memory[add] = r;
	}

	public static Integer getMem(int add) {
		return memory[add];
	}

	private static void branchOp() {
		if (branchFlag&&clock%2==1) {
			stages[0].getNextData().put("PC", addressBranch);
			stages[0].active = true;
			setPC(addressBranch);
			if (addressBranch >= 1024 || memory[addressBranch] == null)
				stages[0].active = false;
			stages[1].active = false;
			stages[2].active = false;
			branchFlag = false;
		}
		
	}

	public static void branchAndJump(int address) {
		branchFlag = true;
		addressBranch = address;
	}

	public static void setPC(int pc) {
		Arc.pc = pc;
	}

	private void addInstruction(Integer instruction) {
		memory[lastInstruction] = instruction;
		lastInstruction++;
	}

	//////////////////

	private void parseInstructions(String pathFile) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(pathFile));
		String record;
		while ((record = br.readLine()) != null) {
			if (record.equals(""))
				break;
			String[] fields = record.split(" ");
			Integer instruction = null;
			switch (fields[0]) {
			case "ADD":
				instruction = 0;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = ParseReg(fields[3], instruction, 3);
				break;
			case "SUB":
				instruction = 1;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = ParseReg(fields[3], instruction, 3);
				break;
			case "MULI":
				instruction = 2;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = parseImmediate(fields[3], instruction);

				break;
			case "ADDI":
				instruction = 3;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = parseImmediate(fields[3], instruction);
				break;
			case "BNE":
				instruction = 4;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = parseImmediate(fields[3], instruction);

				break;
			case "ANDI":
				instruction = 5;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = parseImmediate(fields[3], instruction);
				break;
			case "ORI":
				instruction = 6;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = parseImmediate(fields[3], instruction);

				break;
			case "J":
				instruction = 7;
				instruction = instruction << 28;
				instruction = parseAddAndShift(fields[1], instruction);
				break;
			case "SLL":
				instruction = 8;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = parseAddAndShift(fields[3], instruction);

				break;
			case "SRL":
				instruction = 9;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = parseAddAndShift(fields[3], instruction);
				break;
			case "LW":
				instruction = 10;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = parseImmediate(fields[3], instruction);

				break;
			case "SW":
				instruction = 11;
				instruction = instruction << 28;
				instruction = ParseReg(fields[1], instruction, 1);
				instruction = ParseReg(fields[2], instruction, 2);
				instruction = parseImmediate(fields[3], instruction);
				break;
			default:
				System.out.println(Arrays.toString(fields));
				throw new Exception("Unknown Intsruction");
			}

			addInstruction(instruction);

		}

	}

	private Integer ParseReg(String reg, Integer instruction, int regNum) {
		int loc = Integer.parseInt(reg.substring(1));
		if (regNum == 1)
			loc = loc << 23;
		else if (regNum == 2)
			loc = loc << 18;
		else
			loc = loc << 13;
		return instruction | loc;
	}

	private Integer parseImmediate(String immediate, Integer instruction) {
		int loc = Integer.parseInt(immediate);
		loc = loc & 0b00000000000000111111111111111111;
		return loc | instruction;
	}

	private Integer parseAddAndShift(String address, Integer instruction) {
		int loc = Integer.parseInt(address);
		return loc | instruction;
	}
	public static void printMemAndReg() {
		System.out.println(Arrays.toString(memory));
		System.out.println(Arrays.toString(registerFile));
	}

}
