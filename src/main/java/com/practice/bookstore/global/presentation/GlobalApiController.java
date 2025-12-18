package com.practice.bookstore.global.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class GlobalApiController {
    // 주의 : static이라서 서버 꺼질 때까지 메모리에 계속 남음.
    private static List<String> LEAK_BUCKET = new ArrayList<>();

    @GetMapping("/health")
    public String healthCheck(){
        return "OK";
    }

    @GetMapping("/test/memory-leak")
    public String createMemoryLeak(){
        // 호출할 때마다 1MB 정도의 쓰레기 데이터를 힙 메모리에 쑤셔넣음
        for(int i=0; i<5000; i++){
            LEAK_BUCKET.add("Garbage Data"+ UUID.randomUUID().toString());
        }
        return "Memory Leaking... Size: "+ LEAK_BUCKET.size();
    }
}
