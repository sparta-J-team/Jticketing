package com.project.jticketing.redis;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

	private final RedisService redisService;

	public RedisController(RedisService redisService) {
		this.redisService = redisService;
	}

	@PostMapping("/save")
	public String save(@RequestParam String key, @RequestParam String value) {
		redisService.save(key, value);
		return "Data saved!";
	}

	@GetMapping("/find")
	public String find(@RequestParam String key) {
		return redisService.find(key);
	}

	@DeleteMapping("/delete")
	public String delete(@RequestParam String key) {
		redisService.delete(key);
		return "Data deleted!";
	}

	// 연결 상태 확인 엔드포인트
	@GetMapping("/test-connection")
	public String testConnection() {
		return redisService.testConnection();
	}
}
