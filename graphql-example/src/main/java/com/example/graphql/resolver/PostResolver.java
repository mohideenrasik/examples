package com.example.graphql.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.example.graphql.model.Author;
import com.example.graphql.model.AuthorRepository;
import com.example.graphql.model.Post;
import com.example.graphql.model.PostRepository;

import graphql.schema.DataFetchingEnvironment;

@Component
public class PostResolver implements GraphQLResolver<Post>{

	@Autowired
	private AuthorRepository authorRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	public Author getAuthor(Post post, /* Optional */ DataFetchingEnvironment env) {
		return authorRepository.findById(post.getAuthorId()).get();
	}
	
	public long totalPosts(Post post) {
		return postRepository.count();
	}
	
}
