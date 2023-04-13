package hk.ust.comp3021.person;

import hk.ust.comp3021.resource.Comment;
import hk.ust.comp3021.resource.Label;
import java.util.*;
import java.util.stream.Collectors;

public class User extends Person {
    private final Date registerDate;

    private final ArrayList<Comment> userComments = new ArrayList<>();

    private final ArrayList<Label> userLabels = new ArrayList<>();


    public User(String id, String name, Date registerDate) {
        super(id, name);
        this.registerDate = registerDate;
    }

    public void appendNewComment(Comment comment) {
        userComments.add(comment);
    }

    public ArrayList<Comment> searchCommentByPaperObjID(String id) {
        ArrayList<Comment> res = new ArrayList<>();
        for (Comment comment : userComments) {
            if (comment.getType() == Comment.CommentType.COMMENT_OF_PAPER) {
                if (comment.getCommentObjId().equals(id)) {
                    res.add(comment);
                }
            }
        }
        return res;
    }

    /**
     * Rewrite `searchCommentByPaperObjID` method with Lambda expressions following the original logic.
     * @param the paper id
     * @return the list of comments based on the input id
     */
    public ArrayList<Comment> searchCommentByPaperObjIDByLambda(String id) {
//        ArrayList<Comment> res = new ArrayList<>();
        return(ArrayList<Comment>)userComments.stream()
          .filter(comment->comment.getType() == Comment.CommentType.COMMENT_OF_PAPER&&comment.getCommentObjId().equals(id))
          .collect(Collectors.toList());
    }

    public ArrayList<Comment> searchCommentByCommentObjID(String id) {
        ArrayList<Comment> res = new ArrayList<>();
        for (Comment comment : userComments) {
            if (comment.getType() == Comment.CommentType.COMMENT_OF_COMMENT) {
                if (comment.getCommentObjId().equals(id)) {
                    res.add(comment);
                }
            }
        }
        return res;
    }

    /**
     * Rewrite `searchCommentByCommentObjID` method with Lambda expressions following the original logic.
     * @param the comment id
     * @return the list of comments based on the input id
     */
    public ArrayList<Comment> searchCommentByCommentObjIDByLambda(String id) {
      return(ArrayList<Comment>)userComments.stream()
          .filter(comment->comment.getType() == Comment.CommentType.COMMENT_OF_COMMENT&&comment.getCommentObjId().equals(id))
          .collect(Collectors.toList());
    }

    public void appendNewLabel(Label label) {
        userLabels.add(label);
    }

    public ArrayList<Label> searchLabelByPaperID(String id) {
        ArrayList<Label> res = new ArrayList<>();
        for (Label label : userLabels) {
            if (label.getPaperID().equals(id)) {
                res.add(label);
            }
        }
        return res;
    }

    /**
     * Rewrite `searchLabelByPaperIDByLambda` method with Lambda expressions following the original logic.
     * @param the paper id
     * @return the list of labels based on the input id
     */
    public ArrayList<Label> searchLabelByPaperIDByLambda(String id) {
//        ArrayList<Label> res = new ArrayList<>();
//        return res;
      return( ArrayList<Label>)userLabels.stream()
          .filter(label -> label.getPaperID().equals(id))
          .collect(Collectors.toList());
    }
}
