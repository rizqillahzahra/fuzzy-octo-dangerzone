package fuzzy;

public class MainTest {
	public static void main(String[] args) {
		Room room = new Room(81);
		TempController tc = new TempController(room);
		FuzzyController fc = new FuzzyController(tc, 72);
		
		Thread fct = new Thread(fc);
		Thread tct = new Thread(tc);
		
		tct.start();
		fct.start();
		
		try {
			fct.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Test complete");
	}
}
