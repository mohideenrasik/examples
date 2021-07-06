package com.example.graphql.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.example.graphql.model.Post;
import com.example.graphql.model.PostRepository;

@Component
public class MutationResolver implements GraphQLMutationResolver {

	@Autowired
	private PostRepository postRepository;
	
	public Post writePost(long postId, String title, String text, String category, long authorId) {
		return postRepository.save(new Post(postId, title, text, category, authorId));
	}
}
