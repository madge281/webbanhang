package com.asm.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asm.bean.Account;
import com.asm.bean.RoleDetail;
import com.asm.bean.Account;
import com.asm.dao.AccountRepo;
import com.asm.service.AccountService;
import com.asm.service.AccountService;
import com.asm.service.UploadService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin/rest/accounts")
public class UserRestController {
	@Autowired
	AccountService aService;
	@Autowired
	UploadService uService;

	@GetMapping("")
	public List<Account> getAllAccount() { //Phương thức này trả về danh sách các tài khoản 
	//Phương thức findAll() của aService được gọi để lấy tất cả các tài khoản từ nguồn dữ liệu Kết quả được trả về là danh sách các tài khoản	
		return aService.findAll(); 
	}

	@GetMapping("/{username}")
	/*
	 * Phương thức này nhận vào một tham số username từ đường dẫn URL và trả về một ResponseEntity<Account>
	 * ResponseEntity là một đối tượng đại diện cho phản hồi HTTP và có thể chứa một đối tượng dữ liệu và mã trạng thái
	 */
	public ResponseEntity<Account> getAccount(@PathVariable("username") String username) {
		if (!aService.existsById(username)) { // Kiểm tra xem tài khoản có tồn tại trong db
			return ResponseEntity.notFound().build(); // Nếu tài khoản không tồn tại, phương thức trả về một ResponseEntity với mã trạng thái HTTP 404 Not Found
		} else {
			return ResponseEntity.ok(aService.findByUsername(username));
		}
	}
	
	@GetMapping("/authorities")
	 // Phương thức này trả về một Map<String, Object> chứa thông tin về các tài khoản, vai trò và quyền
	public Map<String, Object> getAuthority(){ 
		Map<String, Object> map = new HashMap<String, Object>();
	// Thêm một cặp key-value vào map với key là "accounts" và value là danh sách các tài khoản được lấy từ phương thức findAll() của aService
		map.put("accounts",aService.findAll());
	// Thêm một cặp key-value vào map với key là "roles" và value là danh sách các vai trò được lấy từ phương thức findAllRole() của aService
		map.put("roles", aService.findAllRole());
	// Thêm một cặp key-value vào map với key là "authorities" và value là danh sách các quyền được lấy từ phương thức findAllAuthorities() của aService
		map.put("authorities",aService.findAllAuthorities());
		return map; // Trả về map chứa thông tin về các tài khoản, vai trò và quyền
	}
	
	@GetMapping("/search")
	// Phương thức này trả về một danh sách các tài khoản và nhận một tham số tùy chọn "kw" từ query string của yêu cầu
	public List<Account> searchAccount(@RequestParam("kw") Optional<String> kw){
		String keyword = kw.orElse(null);	// Gán giá trị của tham số "kw" vào biến keyword, nếu tham số không tồn tại, kw sẽ được gán giá trị null
	// Nếu kw khác null, phương thức findByFullname() sẽ được gọi để tìm kiếm các tài khoản có fullname chứa từ khóa. Danh sách các tài khoản tìm thấy sẽ được trả về
		if(keyword != null) {
			return aService.findByFullname("%"+keyword+"%");
		}else {
	// Nếu keyword là null, phương thức getAllAccount() của cùng controller sẽ được gọi để lấy danh sách tất cả các tài khoản
			return this.getAllAccount();
		}
	}
	
	@GetMapping("/authorities/search")
	// Phương thức này trả về một danh sách các tài khoản và nhận một tham số tùy chọn "kw" từ query string của yêu cầu
	public List<Account> searchAccountByUsername(@RequestParam("kw") Optional<String> kw){
		String keyword = kw.orElse(null); // Gán giá trị của tham số "kw" vào biến keyword Nếu tham số không tồn tại, keyword sẽ được gán giá trị null
	// Nếu keyword khác null, phương thức findByUsernameLike()  sẽ được gọi để tìm kiếm các tài khoản có tên người dùng chứa từ khóa. 
		//Danh sách các tài khoản tìm thấy sẽ được trả về
		if(keyword != null) { 
			return aService.findByUsernameLike("%"+keyword+"%");
		}else {
	// Nếu keyword là null, phương thức getAllAccount() của cùng controller sẽ được gọi để lấy danh sách tất cả các tài khoản
			return this.getAllAccount();
		}
	}
	
	@PostMapping("")
	public ResponseEntity<Account> postAccount(@RequestBody Account Account){
		if(aService.existsById(Account.getUsername())) {
			return ResponseEntity.badRequest().build();
		}else {
			return ResponseEntity.ok(aService.save(Account));
		}
	}
	
	@PostMapping("/authorities")
	public RoleDetail postAuthorities(@RequestBody RoleDetail authority) {
		return aService.saveRoleDetail(authority);
	}
	
	@PutMapping("/{username}")
	public ResponseEntity<Account> putAccount(@PathVariable("username") String username, @RequestBody Account Account){
		if(!aService.existsById(username)) {
			return ResponseEntity.notFound().build();
		}else {
			return ResponseEntity.ok(aService.save(Account));
		}
	}
	@DeleteMapping("/{username}")
	public ResponseEntity<Void> deleteAccount(@PathVariable("username") String username){
		if(!aService.existsById(username)) {
			return ResponseEntity.notFound().build();
		}else {
			Account Account = aService.findByUsername(username);
			String filename = Account.getPhoto();
			System.out.println(filename);
			if(!filename.equalsIgnoreCase("logo.jpg")) {
				uService.delete("account", filename);
			}
			aService.deleteByUsername(username);
			return ResponseEntity.ok().build();
		}
	}
	@DeleteMapping("/authorities/{id}")
	public void deleteAuthorities(@PathVariable("id") Long id) {
		aService.deleteRoleDetail(id);
	}
	
}
