/*
 * Name:			Wallace Coleman
 * Course Name:		Operating Systems
 * Semester:		Spring 2019
 * Assignment:		Assignment 2
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class Assignment2 {
	static ArrayList<Integer> ids = new ArrayList<>();
	static Random rand = new Random();
	static ArrayList<Process> processes = new ArrayList<>();
	static Scanner scan;

	public static void main(String[] args) {
		scan = new Scanner(System.in);
		prepIDs();
		addInitialValues();

		System.out.println("Initial State");
		displayState();

		userInput();

		System.out.println();
		
		System.out.printf("%-5s | %-10s | %-10s | %-15s | %-15s\n", "ID", "Priority", "Burst Time", "Algorithm",
				"Waiting Time");
		System.out.println("-----------------------------------------------------------------");
		RoundRobin p1 = new RoundRobin(processes);
		p1.go();
		System.out.println("-----------------------------------------------------------------");
		Priority p2 = new Priority(processes);
		p2.go();
		System.out.println("-----------------------------------------------------------------");
		SJF p3 = new SJF(processes);
		p3.go();
		
		ArrayList<Algorithm> algorithms = new ArrayList<>();
		algorithms.add(p1);
		algorithms.add(p2);
		algorithms.add(p3);
		
		Comparator<Algorithm> comp = (x, y) -> (int)x.getAverageWait() - (int)y.getAverageWait();
		Collections.sort(algorithms, comp);
		System.out.print("\nAlgorithms shortest to longest: ");
		for(int i = 0; i < algorithms.size(); i++) {
			System.out.print(algorithms.get(i).getName() + " ");
		}
		
	}

	private static void userInput() {
		Scanner input = new Scanner(System.in);

		Boolean notFinished = true;

		while (notFinished) {
			if (ids.size() == 0) {
				System.out.println("No more available IDs");
				notFinished = false;
				break;
			}
			System.out.print("\nEnter enter an id for the process or type done to move on: ");
			String pid = input.next();
			switch (pid) {
			case "done":
			case "Done":
			case "d":
			case "D":
				notFinished = false;
				break;
			default:
				int pid2 = 0;
				int priority = 0;
				int burstLength = 0;
				try {
					pid2 = Integer.parseInt(pid);

					if (ids.contains(pid2)) {
						// moved to bottom
					} else {
						boolean notFound = true;
						while (notFound) {
							System.out.print("ID unavailable. Please enter one of the following: ");
							for (int i = 0; i < ids.size(); i++) {
								System.out.print(ids.get(i) + " ");
							}
							System.out.println("\nProcess ID: ");
							pid2 = input.nextInt();

							if (ids.contains(pid2)) {
								notFound = false;
							}
						}
					}
					boolean notDone = true;
					while (notDone) {
						System.out.print("Enter a priority between 0 and 10: ");
						priority = input.nextInt();

						if (priority >= 0 && priority <= 10) {
							notDone = false;
						}
					}

					notDone = true;
					while (notDone) {
						System.out.print("Enter a burst time between 20 and 100: ");
						burstLength = input.nextInt();

						if (burstLength >= 20 && burstLength <= 100) {
							notDone = false;
						}
					}

					for (int i = 0; i < ids.size(); i++) {
						if (ids.get(i) == pid2) {
							ids.remove(i);
						}
					}

					Process proc = new Process(pid2, priority, burstLength);
					processes.add(proc);
					displayState();
				} catch (Exception e) {
					System.out.println("You must enter an iteger");
				}
			}
		}
	}

	private static void displayState() {
		System.out.printf("%-5s | %-10s | %-15s\n", "ID", "Priority", "Burst-Length");
		System.out.println("---------------------------------------");

		for (int i = 0; i < processes.size(); i++) {
			System.out.printf("%-5d | %-10d | %-15d\n", processes.get(i).getId(), processes.get(i).getPriority(),
					processes.get(i).getIntitalBurstTime());
		}
	}

	private static void addInitialValues() {
		Process proc;
		for (int i = 0; i < 5; i++) {
			proc = new Process(ids.get(ids.size() - 1), rand.nextInt((10) + 1), (rand.nextInt(81) + 20));
			ids.remove(ids.size() - 1);
			processes.add(proc);
		}
	}

	private static void prepIDs() {
		for (int i = 0; i <= 10; i++) {
			ids.add(i);
		}
		Collections.shuffle(ids);
	}
}

class SJF extends Algorithm{
	private ArrayList<Process> processes = new ArrayList<>();
	private double averageWait;
	
	public double getAverageWait() {
		return averageWait;
	}

	public SJF(ArrayList<Process> processes) {
		super();
		for (int i = 0; i < processes.size(); i++) {
			this.processes.add(processes.get(i).copy());
		}
	}
	
	public void go() {
		Comparator<Process> comp = (p1, p2) -> p1.getBurstlength()-p2.getBurstlength();
		Collections.sort(processes, comp);
		int totalWaitTime = 0;
		for(int i = 0; i < processes.size(); i++) {
			//System.out.println(processes.get(i).getPriority());
			for(int j = i + 1; j < processes.size(); j++) {
				processes.get(j).addWaitingTime(processes.get(i).getBurstlength());
			}
			System.out.printf("%-5d | %-10d | %-10d | %-15s | %-15d\n", processes.get(i).getId(),
					processes.get(i).getPriority(), processes.get(i).getIntitalBurstTime(),
					"SJF", processes.get(i).getWaitingTime());
			totalWaitTime +=  processes.get(i).getWaitingTime();
		}
		averageWait = totalWaitTime/(double)processes.size();
		System.out.println("\nAverage waiting time: " + (String.format("%-5.2f", averageWait)));
	}

	@Override
	public String getName() {
		return "SJF";
	}
}

class Priority extends Algorithm {
	private ArrayList<Process> processes = new ArrayList<>();
	private double averageWait;
	
	public Priority(ArrayList<Process> processes) {
		super();
		for (int i = 0; i < processes.size(); i++) {
			this.processes.add(processes.get(i).copy());
		}
	}
	public double getAverageWait() {
		return averageWait;
	}
	public void go() {
		Comparator<Process> comp = (p1, p2) -> p1.getPriority()-p2.getPriority();
		Collections.sort(processes, comp);
		int totalWaitTime = 0;
		for(int i = 0; i < processes.size(); i++) {
			//System.out.println(processes.get(i).getPriority());
			for(int j = i + 1; j < processes.size(); j++) {
				processes.get(j).addWaitingTime(processes.get(i).getBurstlength());
			}
			System.out.printf("%-5d | %-10d | %-10d | %-15s | %-15d\n", processes.get(i).getId(),
					processes.get(i).getPriority(), processes.get(i).getIntitalBurstTime(),
					"Priority", processes.get(i).getWaitingTime());
			totalWaitTime +=  processes.get(i).getWaitingTime();
		}
		averageWait = totalWaitTime/(double)processes.size();
		System.out.println("\nAverage waiting time: " + (String.format("%-5.2f", averageWait)));
	}
	@Override
	public String getName() {
		return "Priority";
	}
}

class RoundRobin extends Algorithm {
	private ArrayList<Process> processes = new ArrayList<>();
	private ArrayList<Process> finishedProcesses = new ArrayList<>();
	private int totalWaitTime = 0;
	private double averageWait = 0;

	public RoundRobin(ArrayList<Process> processes) {
		super();
		for (int i = 0; i < processes.size(); i++) {
			this.processes.add(processes.get(i).copy());
		}
	}
	public double getAverageWait() {
		return averageWait;
	}
	public void go() {
		boolean notDone = true;
		while (notDone) {
			for (int i = 0; i < processes.size(); i++) {
				int duration = processes.get(i).lowerBurstlength(20);
				for (int j = 0; j < processes.size(); j++) {
					if (j != i) {
						processes.get(j).addWaitingTime(duration);
					}
				}
				if (processes.get(i).getBurstlength() == 0) {
					finishedProcesses.add(processes.get(i));
					processes.remove(i);
					i -= 1;
					if (processes.size() == 0) {
						notDone = false;
					}
				}
			}
		}
		
		for (int i = 0; i < finishedProcesses.size(); i++) {
			totalWaitTime += finishedProcesses.get(i).getWaitingTime();
			System.out.printf("%-5d | %-10d | %-10d | %-15s | %-15d\n", finishedProcesses.get(i).getId(),
					finishedProcesses.get(i).getPriority(), finishedProcesses.get(i).getIntitalBurstTime(),
					"Round Robin", finishedProcesses.get(i).getWaitingTime());
		}
		averageWait = totalWaitTime/(double)finishedProcesses.size();
		System.out.println("\nAverage waiting time: " + (String.format("%-5.2f", averageWait)));
	}
	@Override
	public String getName() {
		return "Round Robin";
	}
}

class Process {
	private int id, priority, burstlength, initialBurstTime, waitingTime = 0;

	public Process(int id, int priority, int burstlength) {
		super();
		this.id = id;
		this.priority = priority;
		this.burstlength = burstlength;
		initialBurstTime = burstlength;
	}

	public Process copy() {
		Process temp = new Process(id, priority, burstlength);
		return temp;
	}

	public Object getIntitalBurstTime() {
		return initialBurstTime;
	}

	public int getBurstlength() {
		return burstlength;
	}

	public int lowerBurstlength(int burstlength) {
		if (burstlength > this.burstlength) {
			int timeTaken = this.burstlength;
			this.burstlength = 0;
			return timeTaken;
		} else {
			this.burstlength -= burstlength;
			return burstlength;
		}
	}

	public int getId() {
		return id;
	}

	public int getPriority() {
		return priority;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void addWaitingTime(int waitingTime) {
		// System.out.println(waitingTime);
		this.waitingTime += waitingTime;
	}

}

abstract class Algorithm {
	public abstract double getAverageWait();
	public abstract String getName();
}
