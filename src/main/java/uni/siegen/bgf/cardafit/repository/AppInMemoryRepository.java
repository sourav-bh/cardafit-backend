package uni.siegen.bgf.cardafit.repository;

import org.springframework.scheduling.annotation.Scheduled;

public class AppInMemoryRepository {
	private static AppInMemoryRepository instance = null;
	private int scheduleCount = 0;
	
	private AppInMemoryRepository() {
		scheduleCount = 0;
	}
	
	public static AppInMemoryRepository getInstance() {
		if (instance == null) {
			instance = new AppInMemoryRepository();
		}
		
		return instance;
	}
	
	@Scheduled(cron = "${daily.start.day.scheduled.cron}")
	public void resetDailyCounter() {
		scheduleCount = 0;
	}
	
	public void updateScheduleCount() {
		scheduleCount += 1;
	}
	
	public int getScheduleCount() {
		return scheduleCount;
	}

}
