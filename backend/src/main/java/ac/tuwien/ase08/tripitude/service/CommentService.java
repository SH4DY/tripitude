package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.ICommentDAO;
import ac.tuwien.ase08.tripitude.dao.interfaces.ICoordinateDAO;
import ac.tuwien.ase08.tripitude.entity.Comment;
import ac.tuwien.ase08.tripitude.service.interfaces.ICommentService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("commentService")
public class CommentService implements ICommentService, ICommentDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private ICommentDAO commentDAO;
	
	@Override
	public void add(Comment entity) {
		commentDAO.add(entity);
	}

	@Override
	public void update(Comment entity) {
		commentDAO.update(entity);
	}

	@Override
	public void remove(Comment entity) {
		commentDAO.remove(entity);
	}

	@Override
	public Comment find(Long key) {
		return commentDAO.find(key);
	}

	@Override
	public List<Comment> list() {
		return commentDAO.list();
	}
}
