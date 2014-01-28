package ac.tuwien.ase08.tripitude.dao;

import java.util.Date;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.ICommentDAO;
import ac.tuwien.ase08.tripitude.entity.Comment;

@Repository
public class CommentDAO extends HibernateDAO<Comment, Long> implements ICommentDAO {

	public void add(Comment comment) {
		//set current date
		comment.setCreated(new Date());
		super.add(comment);
	}
}
