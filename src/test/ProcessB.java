package test;

import core.NetworkingProcess;


public class ProcessB {

	public static void main(String args[]) {
		NetworkingProcess np = new NetworkingProcess(5, 2015);
		
		np.addProcess(0, 2014);
		np.addProcess(10, 2016);

		for (int i = 0; i < 3; i++) {
			np.addResource(i);
		}
		
		np.start();
		
	}
}
