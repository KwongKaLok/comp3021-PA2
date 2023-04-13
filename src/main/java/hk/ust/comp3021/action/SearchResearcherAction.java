package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class SearchResearcherAction extends Action {
    public enum SearchResearcherKind {
        PAPER_WITHIN_YEAR,
        JOURNAL_PUBLISH_TIMES,
        KEYWORD_SIMILARITY,
    };

    private String searchFactorX;
    private String searchFactorY;
    private SearchResearcherKind kind;

    private final HashMap<String, List<Paper>> actionResult = new HashMap<String, List<Paper>>();

    public SearchResearcherAction(String id, User user, Date time, String searchFactorX, String searchFactorY, SearchResearcherKind kind) {
        super(id, user, time, ActionType.SEARCH_PAPER);
        this.searchFactorX = searchFactorX;
        this.searchFactorY = searchFactorY;
        this.kind = kind;
    }

    public String getSearchFactorX() {
        return searchFactorX;
    }

    public String getSearchFactorY() {
        return searchFactorY;
    }

    public void setSearchFactorX(String searchFactorX) {
        this.searchFactorX = searchFactorX;
    }

    public void setSearchFactorY(String searchFactorY) {
        this.searchFactorY = searchFactorY;
    }

    public SearchResearcherKind getKind() {
        return kind;
    }

    public void setKind(SearchResearcherKind kind) {
        this.kind = kind;
    }

    public HashMap<String, List<Paper>> getActionResult() {
        return actionResult;
    }

    public void appendToActionResult(String researcher, Paper paper) {
        List<Paper> paperList = this.actionResult.get(researcher);
        if (paperList == null) {
            paperList = new ArrayList<Paper>();
            this.actionResult.put(researcher, paperList);
        }
        paperList.add(paper);
    }

    /**
     * TODO `searchFunc1` indicates the first searching criterion
     *    i.e., Search researchers who publish papers more than or equal to X times in the recent Y years
     * @param null
     * @return `actionResult` that contains the relevant researchers
     */
    public Supplier<HashMap<String, List<Paper>>> searchFunc1=()->{
      int x=Integer.parseInt(searchFactorX);
      int y=Integer.parseInt(searchFactorY);
      Calendar cal = Calendar.getInstance();
      int currentYear = cal.get(Calendar.YEAR);
      // remove all researchers that have less paper than X without checking year
      Iterator<Entry<String, List<Paper>>> researcher = actionResult.entrySet().iterator(); 
      while (researcher.hasNext()) {
          Entry<String, List<Paper>> entry = researcher.next();
          if (entry.getValue().size() < x) {
            researcher.remove();
          }else {
            //Check year
            int yearCountXTimes = 0;
            for (Paper paper : entry.getValue()) {

              if(paper.getYear()>=(currentYear-y)) {
                yearCountXTimes+=1;
              }
            }
            //Check X times in the recent Y years
            if(yearCountXTimes<x) {
              researcher.remove();
            }
          }
      }
      return actionResult;
    };
     

    /**
     * TODO `searchFunc2` indicates the second searching criterion,
     *    i.e., Search researchers whose papers published in the journal X have abstracts of which the length is more than or equal to Y.
     * @param null
     * @return `actionResult` that contains the relevant researchers
     */
    public Supplier<HashMap<String, List<Paper>>> searchFunc2=()->{
      int y=Integer.parseInt(searchFactorY);
      
      Iterator<Entry<String, List<Paper>>> researcher = actionResult.entrySet().iterator(); 
      while (researcher.hasNext()) {
          boolean keepResearcher = false;
          ArrayList<Integer> paperIndex = new ArrayList<Integer>();
          Entry<String, List<Paper>> entry = researcher.next();
          for (Paper paper : entry.getValue()) {
            //Check paper published in the journal X
             if(paper.getJournal() != null && paper.getJournal().equals(searchFactorX)) { 
               //Check abstracts of which the length is more than or equal to Y.
               if(paper.getAbsContent()!=null && paper.getAbsContent().length()>=y) {
                 //keepResearcher: Keep the researcher that satisfy the conditions
                 //paperIndex: Keep the paper that satisfy the conditions
                 keepResearcher = true;
                 paperIndex.add(entry.getValue().indexOf(paper));
               }
             }
          }
          if(!keepResearcher) {
            //Remove researcher that does not satisfy the conditions
            researcher.remove();
          }else {
            //Remove papers that is not in journal X
            for(int i = entry.getValue().size() - 1; i >= 0; i--) {
              if(!paperIndex.contains(i)) {
                entry.getValue().remove(i);
                }
              }
          }
      }
      return actionResult;
    };

    public static int getLevenshteinDistance(String str1, String str2) {
      int m = str1.length();
      int n = str2.length();
      int[][] dp = new int[m+1][n+1];
      for (int i = 0; i <= m; i++) {
          dp[i][0] = i;
      }
      for (int j = 0; j <= n; j++) {
          dp[0][j] = j;
      }
      for (int i = 1; i <= m; i++) {
          for (int j = 1; j <= n; j++) {
              if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                  dp[i][j] = dp[i - 1][j - 1];
              } else {
                  dp[i][j] = Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1])) + 1;
              }
          }
      }
      return dp[m][n];
    }
    
    public double getSimilarity(String str1, String str2) {
      int levenshteinDistance = getLevenshteinDistance(str1,str2);
      int nominator = Math.max(str1.length(), str2.length());
      double divisionResult = (double) levenshteinDistance / (double) nominator;
      return ((1 - divisionResult) * 100);
    }

    /**
     * TODO `searchFunc3` indicates the third searching criterion
     *    i.e., Search researchers whoes keywords have more than or equal to similarity X% as one of those of the researcher Y.
     * @param null
     * @return `actionResult` that contains the relevant researchers
     * PS: 1) In this method, you are required to implement an extra method that calculates the Levenshtein Distance for
     *     two strings S1 and S2, i.e., the edit distance. Based on the Levenshtein Distance, you should calculate their
     *     similarity like `(1 - levenshteinDistance / max(S1.length, S2.length)) * 100`.
     *     2) Note that we need to remove paper(s) from the paper list of whoever are co-authors with the given researcher.
     */
    public Supplier<HashMap<String, List<Paper>>> searchFunc3=()->{
      double x = Double.parseDouble(searchFactorX);      
      ArrayList<String>searchResearcherKeyword = new ArrayList<String>();
      //Get all keywords from researcher Y
      for (Paper paper : actionResult.get(searchFactorY)) {
        if(!paper.getKeywords().isEmpty()) {
          for (String keyword : paper.getKeywords()) {
            searchResearcherKeyword.add(keyword);
          }
        }
      }
      String str1 = String.join(", ", searchResearcherKeyword);
      Iterator<Entry<String, List<Paper>>> researcher = actionResult.entrySet().iterator(); 
      while (researcher.hasNext()) {
        ArrayList<Integer> paperIndexToRemove = new ArrayList<Integer>();
        Entry<String, List<Paper>> entry = researcher.next();
        for(int i=0;i<entry.getValue().size();i++){
          Paper paper = entry.getValue().get(i);
          if(paper.getKeywords().isEmpty() || paper.getAuthors().contains(searchFactorY)) {
            paperIndexToRemove.add(entry.getValue().indexOf(paper));
          }else {
            String str2 = String.join(", ",paper.getKeywords());
            if((int) (getSimilarity(str1,str2))<=x) {
              paperIndexToRemove.add(i);
            }
          }
        }
        for(int i = entry.getValue().size() - 1; i >= 0; i--) {
          if(paperIndexToRemove.contains(i)) {
            entry.getValue().remove(i);
            }
          }
        if(entry.getValue().size()==0) {
          researcher.remove();
        }
      }
      return actionResult;
    };

}
