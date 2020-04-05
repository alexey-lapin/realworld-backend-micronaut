package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.CreateArticle;
import com.github.al.realworld.api.command.CreateArticleResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.bus.CommandHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.TagRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;

import javax.inject.Singleton;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;

@RequiredArgsConstructor
@Singleton
public class CreateArticleHandler implements CommandHandler<CreateArticleResult, CreateArticle> {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final SlugService slugService;

    @Transactional
    @Override
    public CreateArticleResult handle(CreateArticle command) {
        Optional<Article> articleByTitleOptional = articleRepository.findByTitle(command.getTitle());
        if (articleByTitleOptional.isPresent()) {
            throw invalidRequest("article [title=%s] already exists", command.getTitle());
        }

        Profile currentProfile = userRepository.findByUsername(command.getCurrentUsername())
                .map(User::getProfile)
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", command.getCurrentUsername()));

        ZonedDateTime now = ZonedDateTime.now();

        Article.ArticleBuilder articleBuilder = Article.builder()
                .id(UUID.randomUUID())
                .slug(slugService.makeSlug(command.getTitle()))
                .title(command.getTitle())
                .description(command.getDescription())
                .body(command.getBody())
                .createdAt(now)
                .updatedAt(now)
                .author(currentProfile);

        command.getTagList().stream()
                .map(t -> tagRepository.findByName(t).orElseGet(() -> new Tag(t)))
                .forEach(articleBuilder::tag);

        Article savedArticle = articleRepository.save(articleBuilder.build());

        return new CreateArticleResult(ArticleAssembler.assemble(savedArticle, currentProfile));
    }
}
