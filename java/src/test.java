import java.io.IOException;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ModularityOptimizer modOpt = new ModularityOptimizer();
		try {
			System.out.println("Java Testing Begins!");
			// String testresult = modOpt.test("..\\python\\networkInfo.txt","..\\python\\output.txt","1","1","2","1","10","336","1","..\\python\\Qsupply.txt","..\\python\\Qdemand.txt","..\\python\\SVQ.txt");
			String testresult = modOpt.test("C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\networkInfo.txt","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\output.txt","1","1","2","2","10","336","1","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\Qsupply.txt","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\Qdemand.txt","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\SVQ.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Java Testing Ends!");
	}

}
