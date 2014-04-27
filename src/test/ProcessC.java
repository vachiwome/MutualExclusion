package test;

import core.NetworkingProcess;


public class ProcessC {

	public static void main(String args[]) {
		NetworkingProcess np = new NetworkingProcess(10, 2016);
		
		np.addProcess(0, 2014);
		np.addProcess(5, 2015);

		for (int i = 0; i < 3; i++) {
			np.addResource(i);
		}
		
		np.start();
		
	}
}
