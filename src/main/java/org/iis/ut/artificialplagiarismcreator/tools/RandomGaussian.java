package org.iis.ut.artificialplagiarismcreator.tools;
import java.util.Random;


public final class RandomGaussian {
  
  public static void main(String... aArgs){
    RandomGaussian gaussian = new RandomGaussian();
    Integer MEAN = 100; 
    Integer VARIANCE = 5;
    for (int idx = 1; idx <= 10; ++idx){
      log("Generated : " + gaussian.getGaussian(MEAN, VARIANCE));
    }
  }
    
  private Random fRandom = new Random();
  
  public Double getGaussian(Integer caseNormalDistMean, Integer caseNormalDistStdev){
    return caseNormalDistMean + fRandom.nextGaussian() * caseNormalDistStdev;
  }

  private static void log(Object aMsg){
    System.out.println(String.valueOf(aMsg));
  }
} 