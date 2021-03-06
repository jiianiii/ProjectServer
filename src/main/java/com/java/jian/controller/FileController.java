package com.java.jian.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.java.jian.dao.DaoInterface;
import com.java.jian.util.FinalUtil;
import com.java.jian.util.HttpUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class FileController {

	@Autowired
	DaoInterface di;
	
	/**subPage 이동 경로**/
	@RequestMapping("/sub")
	public String sub() {
		return "sub/subpage";
	}
		
	/**슬라이드 & button 이용*******************************************************/
	@RequestMapping("/sbl")
	public ModelAndView btnld() {
		HashMap<String, Object> param = new HashMap<String, Object>();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		param.put("sqlType", "board.selectSBL");
		param.put("sql","selectList");
		
		List list = (List) di.call(param);	
		resultMap.put("list",list); 	
//		System.out.println(resultMap);
		return HttpUtil.makeJsonView(resultMap);
	}
	
	/**여행지 리스트 *********************************************/
	@RequestMapping("/vlist")
	public ModelAndView vlist(HttpServletRequest req) {

		HashMap<String, Object> param = new HashMap<String, Object>();

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		param.put("sqlType", "board.youtubeList");
		param.put("sql","selectList");
		
		List list = (List) di.call(param);	
		resultMap.put("list",list); 
	
//		System.out.println(resultMap);
		return HttpUtil.makeJsonView(resultMap);
	}
	
	/**여행지 리스트 *********************************************/
	@RequestMapping("/dld")
	public ModelAndView dld(HttpServletRequest req) {
		String boardType = req.getParameter("go");
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("boardType", boardType);		
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		param.put("sqlType", "board.selectTypeList");
		param.put("sql","selectList");
		
		List list = (List) di.call(param);	
		resultMap.put("list",list); 
	
//		System.out.println(resultMap);
		return HttpUtil.makeJsonView(resultMap);
	}

	
	/**수정하기 위한 리스트 화면******************************************/
	@RequestMapping("/bList")
	public String bList() {
		return "/board/list";
	}
	
	//(리스트 보여주고 _수정/ 삭제 페이지로 갈 수 있음)
	@RequestMapping("/bld")
	public ModelAndView bld(HttpServletRequest req) {
		String boardNo = req.getParameter("boardNo");

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("boardNo", boardNo);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		param.put("sqlType", "board.boardOne");
		param.put("sql", "selectOne");
		resultMap.put("boardData", di.call(param));

		param.put("sqlType", "board.fileOne");
		param.put("sql", "selectList");
		resultMap.put("filesData", di.call(param));

		return HttpUtil.makeJsonView(resultMap);
	}
	
	//fileNo로 slide내용 보여줌.....
	@RequestMapping("/sld")
	public ModelAndView sld(HttpServletRequest req) {
		String fileNo = req.getParameter("fileNo");

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("fileNo", fileNo);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		param.put("sqlType", "board.selectSBLOne");
		param.put("sql", "selectOne");
		resultMap.put("fileData", di.call(param));

		//System.out.println(resultMap);
		return HttpUtil.makeJsonView(resultMap);
	}
	
	
	
	/**수정*********************************************/
	@RequestMapping("/bSelect")
	public String bSelect() {
		return "/board/detail";
	}

	@RequestMapping("/bUpdate")
	public String bUpdate(HttpServletRequest req, RedirectAttributes ra) {
		HashMap<String, Object> paramMap = HttpUtil.getParamMap(req);
		paramMap.put("sqlType", "board.boardOne");
		paramMap.put("sql", "selectOne");
//		HashMap<String, Object> resultMap = (HashMap<String, Object>) di.call(paramMap);
		ra.addAttribute("boardNo", paramMap.get("boardNo"));		
		return "/board/update";
	}

	@RequestMapping("/bud")
	public ModelAndView bud(HttpServletRequest req) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String boardNo = req.getParameter("boardNo");
		String boardTitle = req.getParameter("boardTitle");
		String boardContents = req.getParameter("boardContents");
		String data = req.getParameter("data");
		String type = req.getParameter("type");
		System.out.println(type);
		
//		int fileNo = Integer.parseInt(req.getParameter("fileNo"));
		/***board 내용 추가 및 수정****************************************************************************/
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("boardNo", boardNo);
		params.put("boardTitle", boardTitle);
		params.put("boardContents", boardContents);

		params.put("sqlType", "board.boardUpdate");
		params.put("sql", "update");
		int status = (int) di.call(params);
		// int status = session.update("board.boardUpdate", params);
		/********************************************************************************************/
		System.out.println(status);
		if (status == 1) {
			
			if("1".equals(type)) {
				List<JSONObject> dataList = JSONArray.fromObject(data);
				for (int i = 0; i < dataList.size(); i++) {
					JSONObject j = dataList.get(i);
					params = new HashMap<String, Object>();
					params.put("fileNo", j.get("fileNo"));
					params.put("fileName", j.get("fileName"));
					params.put("fileURL", FinalUtil.FILE_DNS + j.get("fileURL"));
					params.put("sqlType", "board.filesUpdate");
					params.put("sql", "update");
					status = (int) di.call(params);
				}
			}
			
			if (status == 1) {
				map.put("msg", "글수정이 완료 되었습니다.");
				map.put("status", FinalUtil.OK);
				map.put("boardNo", boardNo);
			} else {
				map.put("msg", "첨부파일 오류 발생.");
			}
		}else {
			map.put("msg", "글 작성 시 오류 발생.");
		}
		
		
		return HttpUtil.makeJsonView(map);
	}
	
	
	//list 파일업데이트!!!!
	@RequestMapping("/fUpdate")
	public String fUpdate(HttpSession session, HttpServletRequest req, RedirectAttributes ra) {
		HashMap<String, Object> paramMap = HttpUtil.getParamMap(req);
		paramMap.put("sqlType", "board.selectSBLOne");
		paramMap.put("sql", "selectOne");
//		HashMap<String, Object> resultMap = (HashMap<String, Object>) di.call(paramMap);
		ra.addAttribute("fileNo", paramMap.get("fileNo"));
		
		System.out.println("ra" + ra);
		return "board/updateFile";
	}
	
	@RequestMapping("/fud")
	public ModelAndView fud(HttpServletRequest req) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		int fileNo = Integer.parseInt( req.getParameter("fileNo"));
		String fileName = req.getParameter("fileName");		
		String data = req.getParameter("data");

		int status = 0;
		List<JSONObject> dataList = JSONArray.fromObject(data);
			
		
			for (int i = 0; i < dataList.size(); i++) {

				String fileURL = dataList.get(i).get("fileURL").toString();
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("fileNo", fileNo);
				params.put("fileName", fileName);
				params.put("fileURL", FinalUtil.FILE_DNS + fileURL);
				
				params.put("sqlType", "board.filesUpdate");
				params.put("sql", "update");
				
				System.out.println("params : "+ params);
				
				status = (int)di.call(params);				
//				System.out.println(status);
//				session.update("board.filesDel", delList.get(i));
			}
			
			if (status == 1) {
				map.put("msg", "글수정이 완료 되었습니다.");
				map.put("status", FinalUtil.OK);
				map.put("fileNo", fileNo);
			} else {
				map.put("msg", "첨부파일 오류 발생.");
			}
			
		return HttpUtil.makeJsonView(map);
	}
	
	/***관리자 페이지에서 전체 board리스트 보기********************************************/
	@RequestMapping("/boardList")
	public String boardList() {
		return "/board/allList";
	}
	
	@RequestMapping("/allList")
	public ModelAndView allList(HttpServletRequest req) {
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		param.put("sqlType", "board.boardList");
		param.put("sql","selectList");
		
		List list = (List) di.call(param);	
		resultMap.put("list",list); 
	
		
		System.out.println(resultMap);
		return HttpUtil.makeJsonView(resultMap);
	}
	
	@RequestMapping("/binsert")
	public String binsert() {
		return "/board/insert";
	}
	
	@RequestMapping("/bid")
	public ModelAndView bid(HttpServletRequest req) {		
		
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> params = new HashMap<String, Object>();
			
	         int status = 0;
	         //업로드시킨 파일 데이터 값을 받아, JSON 타입으로 변환시켜 'dataList'에 담기
	         String data = req.getParameter("data");	
	         List<Map<String, Object>> dataList = JSONArray.fromObject(data);    
	         	         
	         for(int i = 0; i < dataList.size(); i++) {
	            String fileName = dataList.get(i).get("fileName").toString();
	            String fileURL = dataList.get(i).get("fileURL").toString();
	           
	            //파일 입력하기 위함
	            HashMap<String, Object> fileMap = new HashMap<String, Object>();
	            fileMap.put("fileName", fileName);
	            fileMap.put("fileURL", FinalUtil.FILE_DNS + fileURL);
	            fileMap.put("sqlType", "board.fileInsert");
	            fileMap.put("sql", "insert");	            
	            status = (int) di.call(fileMap);	            
//	            System.out.println(status);
	         }
	         
	         	//파일 입력이 성공되면
	            if(status == 1) {
	               params = new HashMap<String, Object>();
	               params.put("sqlType", "board.getFileNo");
	               params.put("sql", "selectOne");
	               int fileNo = (int) di.call(params);
	               	               
	               HashMap<String, Object> boardparams = new HashMap<String, Object>();
	               String boardTitle = req.getParameter("boardTitle");
	               String boardContents = req.getParameter("boardContents");
	               String boardType = req.getParameter("boardType");
	               boardparams.put("boardTitle", boardTitle);
	               boardparams.put("boardContents", boardContents);
	               boardparams.put("boardType", boardType);
	               boardparams.put("fileNo", fileNo);
	               boardparams.put("sqlType", "board.boardInsert");
	               boardparams.put("sql", "insert");
	               status = (int) di.call(boardparams);
	    
	               if(status == 1) {
	                  map.put("msg", "글작성이 완료 되었습니다.");
	                  map.put("status", FinalUtil.OK);
	               }else {
	                  map.put("msg", "게시글 오류 발생.");
	               }
	            } else {
	               map.put("msg", "글 작성 시 오류 발생.");
	            }
	            return HttpUtil.makeJsonView(map);
	      }
	
	@RequestMapping("/bDel")
	public String bDel(HttpServletRequest req) {
		String boardNo = req.getParameter("boardNo");
		String boardType = req.getParameter("go");
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("boardNo", boardNo);

		param.put("sqlType", "board.boardDel");
		param.put("sql", "update");
		int status1 = (int) di.call(param);
		System.out.println(status1);
		
		param.put("sqlType", "board.filesBoardDel");
		param.put("sql", "update");
		int status2 = (int) di.call(param);
		System.out.println(status2);
		

		return "redirect:/main";
	}

	
}