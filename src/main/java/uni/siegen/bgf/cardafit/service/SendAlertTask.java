package uni.siegen.bgf.cardafit.service;

import java.util.Date;

import uni.siegen.bgf.cardafit.model.User;

public class SendAlertTask implements Runnable {
    private User userInfo;
    
    public SendAlertTask(User userInfo) {
        this.userInfo = userInfo;
    }
    
    @Override
    public void run() {
        System.out.println(new Date()+" Runnable Task with "+userInfo.getUserName()
          +" on thread "+Thread.currentThread().getName());
    }
}