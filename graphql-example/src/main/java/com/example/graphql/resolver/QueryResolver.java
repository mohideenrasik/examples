package com.example.graphql.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.example.graphql.model.Post;
import com.example.graphql.model.PostRepository;

import graphql.schema.DataFetchingEnvironment;

@Component
public class QueryResolver implements GraphQLQueryResolver {
	
	@Autowired
	private PostRepository postRepository;
	
	public List<Post> recentPosts(int count, int offset, /* Optional */ DataFetchingEnvironment env) {
		return postRepository.findAll();
	}

}
