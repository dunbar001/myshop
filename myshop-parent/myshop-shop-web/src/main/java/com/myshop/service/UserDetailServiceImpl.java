package com.myshop.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.myshop.pojo.TbSeller;
import com.myshop.sellergoods.service.SellerService;

public class UserDetailServiceImpl implements UserDetailsService {

	private SellerService sellerService;
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("开始校验");
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		TbSeller tbSeller = sellerService.findOne(username);
		System.out.println(tbSeller);
		if(tbSeller!=null){
			if(tbSeller.getStatus().equals("1")){
				return new User(username, tbSeller.getPassword(), authorities);
			}
		}
		return null;
	}

}
