package com.familytoto.familytotoProject.board.dao;

import java.util.List;

import com.familytoto.familytotoProject.board.domain.BoardVO;
import com.familytoto.familytotoProject.board.domain.PagingVO;

public interface BoardDao {
	// 게시글 등록
	int insertCustBoard(BoardVO vo);
	
	// 게시글수정
	int updateBoard(BoardVO vo);
	
	// 게시글 삭제
	int updateDeleteBoard(BoardVO vo);
	
	// 게시글 보기
	BoardVO getShowBoard(BoardVO vo);
	
	// 페이징
	public int getBoardListCnt() throws Exception;
	
	public List<BoardVO> getBoardList(PagingVO pagination) throws Exception;
}