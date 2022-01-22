package com.poscoict.mysite.mvc.board;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.poscoict.mysite.dao.BoardDao;
import com.poscoict.mysite.vo.BoardVo;
import com.poscoict.web.mvc.Action;
import com.poscoict.web.util.MvcUtil;

public class ListAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("------------");
		int pageCount = 5; // 한 페이지에 페이징 몇 개?
		int listCount = 5; // 한 페이지에 리스트 몇 개?
		int currentPage = 1; // 현재 페이지
		int startPage = 1; // 시작 페이징
		int nextPage = -1; // -1이면 next가 없다.
		
		int count = 0;	//	리스트 개수
		int totalPage = 1; // 총 페이지
		int endPage = 0; // 끝나는 페이징
		HashMap<String, Integer> m = new HashMap<String, Integer>();
		
		//	현재 페이지 번호
		if (request.getParameter("page") != null) {
			currentPage = Integer.parseInt(request.getParameter("page"));
		}

		// 키워드 검색 여부
		String input;
		String input2;	//	키워드 검색시 '삭제되었습니다.'안 보이게
		String limit = "";
		List<BoardVo> list = null;
		if (request.getParameter("kwd") == null && request.getParameter("kwd2")==null) {	//	키워드 검색 아니 경우
			input = "";
			input2 = "";
			count = new BoardDao().CountList();
			m.put("serach", 0);
		} else {	//	키워드 검색인 경우
			if(request.getParameter("kwd")!=null) {
				input = request.getParameter("kwd");	
				System.out.println("키워드");
				currentPage = 1;
			}
			else {
				input = request.getParameter("kwd2");
				System.out.println("키워드ㅌ");
			}
			input2 = " and reg_date != '0000-00-00 00:00:00'";
			list = new BoardDao().findAll("", "\\"+"\\" + input,input2);
			count = list.size();
			m.put("serach", 1);
		}

		System.out.println("count " + count );
		System.out.println(input);
		int endcount = listCount;
		totalPage = ((int) Math.ceil(count / (double) listCount)); 
		endPage = ((int) Math.ceil(totalPage / (double) pageCount));
		if(count <= listCount)
			limit = "";
		else {
			if(currentPage == totalPage) {
				if(count%listCount != 0)
					endcount = count%listCount;
			}
			limit = " LIMIT "+  (currentPage - 1)*listCount + " , " + endcount;
		}
		list = new BoardDao().findAll(limit, input, input2);
		
		if (totalPage <= pageCount) {
			endPage = totalPage;
			startPage = 1;
		} else {
			endPage = ((int) Math.ceil(currentPage / (double) pageCount)) * pageCount;
			startPage = (endPage - pageCount) + 1;
		}
		nextPage = endPage + 1;

		if (endPage > totalPage) {
			endPage = totalPage;
		}

		int prePage = startPage - 1; // 이전 페이징
		int startnum = count - (currentPage-1)*listCount;
		m.put("pageCount", pageCount);
		m.put("listCount", listCount);
		m.put("currentPage", currentPage);
		m.put("totalPage", totalPage);
		m.put("startPage", startPage);
		m.put("endPage", endPage);
		m.put("prePage", prePage);
		m.put("nextPage", nextPage);
		m.put("totalList", count);
		m.put("startnum", startnum);
		
		
		System.out.println(m.toString());
		request.setAttribute("m", m);
		request.setAttribute("list", list);
		request.setAttribute("kwd", input);
		System.out.println("키워드 "+ input);
		MvcUtil.forward("board/list", request, response);
	}


}
