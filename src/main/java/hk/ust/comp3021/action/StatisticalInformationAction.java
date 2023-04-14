package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatisticalInformationAction extends Action {
  public enum InfoKind {
    AVERAGE, MAXIMAL,
  };

  private InfoKind kind;

  private final Map<String, Double> actionResult = new HashMap<String, Double>();

  public StatisticalInformationAction(String id, User user, Date time, InfoKind kind) {
    super(id, user, time, ActionType.STATISTICAL_INFO);
    this.kind = kind;
  }

  public InfoKind getKind() {
    return kind;
  }

  public void setKind(InfoKind kind) {
    this.kind = kind;
  }

  public Map<String, Double> getActionResult() {
    return actionResult;
  }

  public void appendToActionResult(String key, Double value) {
    this.actionResult.put(key, value);
  }

  /**
   * TODO `obtainer1` indicates the first profiling criterion, i.e., Obtain the
   * average number of papers published by researchers per year.
   * @param a list of papers to be profiled
   * @return `actionResult` that contains the target result
   */
  public Function<List<Paper>, Map<String, Double>> obtainer1 = (List<Paper> papers) -> {
    ArrayList<String> researchers = new ArrayList<String>();
    for(Paper paper:papers) {
      for(String paperAuthor:paper.getAuthors()) {
        if(!researchers.contains(paperAuthor)) {
          researchers.add(paperAuthor);
        }
      }
    }
    for (String researcher:researchers) {
      List<Paper>researcherPaperList = new ArrayList<Paper>();
      List<Integer>paperYearList = new ArrayList<Integer>();
      for (Paper paper:papers) {
        if(paper.getAuthors().contains(researcher)) {
          researcherPaperList.add(paper);
          if(!paperYearList.contains(paper.getYear())) {
            paperYearList.add(paper.getYear());
          }
        }
      }
      double averagePaperPerYear = (double)researcherPaperList.size()/(double)paperYearList.size();
      actionResult.put(researcher, averagePaperPerYear);
    }
    return actionResult;
  };

  /**
   * TODO `obtainer2` indicates the second profiling criterion, i.e., Obtain the
   * journals that receive the most papers every year.
   * @param a list of papers to be profiled
   * @return `actionResult` that contains the target result PS1: If two journals
   *         receive the same number of papers in a given year, then we take the
   *         default order. PS2: We keep the chronological order of year so that
   *         the results of the subsequent year will replace the results of the
   *         previous year if one journal receives the most papers in two or more
   *         different years.
   */
  public Function<List<Paper>, Map<String, Double>> obtainer2=(List<Paper>papers)->{
    ArrayList<Integer> years = new ArrayList<Integer>();
    for(Paper paper:papers) {
        if(!years.contains(paper.getYear())) {
          years.add(paper.getYear());
        }
    }
    years.forEach(year->{
      Map<String , Long>yearJournal = papers.stream()
          .filter(paper->paper.getYear()==year && paper.getJournal()!=null)
          .collect(Collectors.groupingBy(Paper::getJournal,Collectors.counting()));
      if(yearJournal.size()>0) {
        Map.Entry<String, Long> maxJournal = yearJournal.entrySet().stream().max(Map.Entry.comparingByValue()).get();
        actionResult.merge(maxJournal.getKey(), (double)maxJournal.getValue(), Double::max);
      }
    });
    return actionResult;
  };
}
