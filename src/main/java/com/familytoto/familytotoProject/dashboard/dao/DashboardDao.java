package com.familytoto.familytotoProject.dashboard.dao;

import java.util.List;

import com.familytoto.familytotoProject.board.domain.BoardVO;
import com.familytoto.familytotoProject.charge.domain.CreditVO;
import com.familytoto.familytotoProject.comment.domain.CommentVO;
import com.familytoto.familytotoProject.creditShop.domain.MileageVO;
import com.familytoto.familytotoProject.creditShop.domain.ProductCommentVO;
import com.familytoto.familytotoProject.exp.domain.ExpVO;
import com.familytoto.familytotoProject.toto.domain.SportsVO;

public interface DashboardDao {
	// 최근크레딧 리스트 가져오기
	List<CreditVO> listRecentCredit(int familyCustNo);
	
	// 최근마일리지 리스트 가져오기
	List<MileageVO> listRecentMileage(int familyCustNo);
	
	// 최근경험치 리스트 가져오기
	List<ExpVO> listRecentExp(int familyCustNo);
	
	// 크레딧 가져오기
	int getTotalCredit(int familyCustNo);
	
	// 마일리지 가져오기
	int getTotalMileage(int familyCustNo);
	
	// 경험치 가져오기
	int getTotalExp(int familyCustNo);
	
	// 레벨 가져오기
	int getCustLevel(int familyCustNo);
	
	// 크레딧 테이블가져오기
	List<CreditVO> listCreditTable(int familyCustNo);
	
	// 마일리지 테이블가져오기
	List<MileageVO> listMileageTable(int familyCustNo);
	
	// 경험치 테이블가져오기
	List<MileageVO> listExpTable(int familyCustNo);
	
	// 토토 배팅한 리스트 가져오기
	List<SportsVO> listBettingGroup(int familyCustNo);
	
	// 작성 게시글 개수 가져오기
	int getBoardCnt(int familyCustNo);
	
	// 작성 댓글 개수 가져오기
	int getCommentCnt(int familyCustNo);
	
	// 작성 상품평 개수 가져오기
	int getProductCommentCnt(int familyCustNo);
	
	// 작성된 게시글 가져오기
	List<BoardVO> listRegisteredBoard(int familyCustNo);
	
	// 작성된 댓글 가져오기
	List<CommentVO> listRegisteredComment(int familyCustNo);
	
	// 작성된 상품평 가져오기
	List<ProductCommentVO> listRegisteredProductComment(int familyCustNo);
}
