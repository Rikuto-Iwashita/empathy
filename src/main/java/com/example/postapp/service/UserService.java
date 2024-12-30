package com.example.postapp.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.postapp.model.User;
import com.example.postapp.repository.UserRepository;
@Service
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザー名が見つかりません: " + username));
    }
    
    //年齢を取得
    public int getUserAge(User user) {
    	//LocalDate型に変換
    	LocalDate birthDate = convertToDate(user.getDateOfBirth());
    	return calculateAge(birthDate);
    }
    
    //世代別登録数を取得
    public Map<String, Long> getAgeGroupCounts() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .collect(Collectors.groupingBy(user -> getAgeGroup(user), Collectors.counting()));
    }

    //ユーザーの年齢グループを返す
    public String getAgeGroup(User user) {
    	int age = calculateAge(convertToDate(user.getDateOfBirth()));
    	
    	if(age >= 13 && age <= 18) {return "13-18歳";}
    	if(age >= 19 && age <= 24) {return "19-24歳";}
    	if(age >= 25 && age <= 29) {return "25-29歳";}
    	if(age >= 30 && age <= 39) {return "30-39歳";}
    	return "40歳以上";
    }
    
    //年齢を計算
    public int calculateAge(LocalDate birthDate) {
    	if(birthDate == null) {
    		return 0;//nullの場合は0を返す。
    	}
    	return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    //文字列をLocalDateに変換
    public LocalDate convertToDate(String dateOfBirth) {
    	try {
			return LocalDate.parse(dateOfBirth);
		} catch (Exception e) {
			return null;//無効な場合はnull
		}
    }
    
    //直接年齢から世代を取得するためのメソッド。getAgeGroupと同じようなもの
    public String getAgeGroupFormAge(int age) {
        if (age >= 13 && age <= 18) return "13-18歳";
        if (age >= 19 && age <= 24) return "19-24歳";
        if (age >= 25 && age <= 29) return "25-29歳";
        if (age >= 30 && age <= 39) return "30-39歳";
        return "40歳以上";
    }
    
    //性別フィルタリング
    public List<User> filterUsersByGender(String gender) {
    	if("指定なし".equals(gender)) {
    		return userRepository.findAll();
    	}
    	return userRepository.findAll().stream()
    			.filter(user -> gender.equals(user.getGender()))
    			.collect(Collectors.toList());
    }
}