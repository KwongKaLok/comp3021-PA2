package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.action.SortPaperAction.SortBase;
import hk.ust.comp3021.person.User;
import java.util.*;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SortPaperAction extends Action {
  public enum SortBase {
    ID, TITLE, AUTHOR, JOURNAL,
  };

  public enum SortKind {
    ASCENDING, DESCENDING,
  };

  private SortBase base;

  private SortKind kind;

  private final List<Paper> actionResult = new ArrayList<>();

  public SortPaperAction(String id, User user, Date time, SortBase base, SortKind kind) {
    super(id, user, time, ActionType.SORT_PAPER);
    this.base = base;
    this.kind = kind;
  }

  public SortBase getBase() {
    return base;
  }

  public void setBase(SortBase base) {
    this.base = base;
  }

  public SortKind getKind() {
    return kind;
  }

  public void setKind(SortKind kind) {
    this.kind = kind;
  }

  public List<Paper> getActionResult() {
    return actionResult;
  }

  public void appendToActionResult(Paper paper) {
    this.actionResult.add(paper);
  }

  /**
   * TODO `appendToActionResultByLambda` appends one paper into `actionResult`
   * each time.
   * 
   * @param paper to be appended into `actionResult`
   * @return null
   */
  public Consumer<Paper> appendToActionResultByLambda = paper -> this.actionResult.add(paper);

  /**
   * TODO `kindPredicate` determine whether the sort kind is
   * `SortKind.DESCENDING`.
   * 
   * @param kind to be compared with `SortKind.DESCENDING`
   * @return boolean variable that indicates whether they are equal
   */
  public Predicate<SortKind> kindPredicate = SortKind -> SortKind == SortKind.DESCENDING;

  /**
   * TODO `comparator` requires you to implement four custom comparators for
   * different scenarios.
   * 
   * @param paper to be sorted
   * @return Given the sort base, there are three conditions for the return: 1) if
   *         a = b then return 0; 2) if a > b, then return 1; 3) if a < b, then
   *         return -1; PS1: if a = null, then a is considered as smaller than
   *         non-null b; PS2: if a and b are both null, then they are considered
   *         equal; PS3: for the author comparison, we should compose the string
   *         of the author names separated by commas.
   */
//    public Comparator<Paper> comparator;
  public Comparator<Paper> comparator = new Comparator<Paper>() {
    @Override
    public int compare(Paper paper1, Paper paper2) {
      int result = 0;
      switch (base) {
      case ID:
        if (paper1 == null && paper2 != null) {
          result = -1;
        } else if (paper1 != null && paper2 == null) {
          result = 1;
        } else if (paper1 == null && paper2 == null) {
          result = 0;
        } else {
          result = paper1.getPaperID().compareTo(paper2.getPaperID());
          if (result > 0) {
            result = 1;
          } else if (result < 0) {
            result = -1;
          }
//                      
        }
        break;
      case TITLE:
        if ((paper1 == null && paper2 != null) || (paper1.getTitle() == null && paper2.getTitle() != null)) {
          result = -1;
        } else if ((paper1 != null && paper2 == null) || (paper1.getTitle() != null && paper2.getTitle() == null)) {
          result = 1;
        } else if ((paper1 == null && paper2 == null) || (paper1.getTitle() == null && paper2.getTitle() == null)) {
          result = 0;
        } else {
          result = paper1.getTitle().compareTo(paper2.getTitle());
          if (result > 0) {
            result = 1;
          } else if (result < 0) {
            result = -1;
          }
        }
        break;
      case AUTHOR:
        if ((paper1 == null && paper2 != null) || (paper1.getAuthors().isEmpty() && !paper2.getAuthors().isEmpty())) {
          result = -1;
        } else if ((paper1 != null && paper2 == null) || (!paper1.getAuthors().isEmpty() && paper2.getAuthors().isEmpty())) {
          result = 1;
        } else if ((paper1 == null && paper2 == null) || (paper1.getAuthors().isEmpty() && paper2.getAuthors().isEmpty())) {
          result = 0;
        } else {
          String authors1 = String.join(",", paper1.getAuthors());
          String authors2 = String.join(",", paper2.getAuthors());
          result = authors1.compareTo(authors2);
          if (result > 0) {
            result = 1;
          } else if (result < 0) {
            result = -1;
          }
        }
        break;
      case JOURNAL:
        if ((paper1 == null && paper2 != null) || (paper1.getJournal() == null && paper2.getJournal() != null)) {
          result = -1;
        } else if ((paper1 != null && paper2 == null) || (paper1.getJournal() != null && paper2.getJournal() == null)) {
          result = 1;
        } else if ((paper1 == null && paper2 == null) || (paper1.getJournal() == null && paper2.getJournal() == null)) {
          result = 0;
        } else {
          result = paper1.getJournal().compareTo(paper2.getJournal());
          if (result > 0) {
            result = 1;
          } else if (result < 0) {
            result = -1;
          }
        }
        break;
      }
      return result;
    }
  };

  /**
   * TODO `sortFunc` provides a unified interface for sorting papers
   * 
   * @param a list of papers to be sorted into `actionResult`
   * @return `actionResult` that contains the papers sorted in the specified order
   */
  public Supplier<List<Paper>> sortFunc = () -> {
    actionResult.sort(comparator);
    if (this.kind == SortKind.DESCENDING) {
      Collections.reverse(actionResult);
    }

    return actionResult;
  };

}
