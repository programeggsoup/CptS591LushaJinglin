import java.io.IOException;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ModularityOptimizer modOpt = new ModularityOptimizer();
		try {
			String testresult = modOpt.test("C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\networkInfo.txt","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\output.txt","1","1","2","10","10","336","1","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\Qsupply.txt","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\Qdemand.txt","C:\\Users\\user\\OneDrive\\Documents\\eclipse-workspace\\CptS591LushaJinglin\\python\\SVQ.txt");
			System.out.println("I'm trying!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Hello");
	}

}
