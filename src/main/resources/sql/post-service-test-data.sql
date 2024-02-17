INSERT INTO `users` (`id`, `email`, `nickname`, `address`, `certification_code`, `status`, `last_login_at`)
VALUES (1, '2jigoo@naver.com', '2jigoo', 'Seoul', 'aaaaa-aaaaa-aaaaa-aaaaa', 'ACTIVE', 0);

INSERT INTO `users` (`id`, `email`, `nickname`, `address`, `certification_code`, `status`, `last_login_at`)
VALUES (2, '2jigoo2@naver.com', '2jigoo2', 'Seoul', 'aaaaa-aaaaa-aaaaa-aaaaa', 'PENDING', 0);

INSERT INTO `posts` (`id`, `content`, `created_at`, `modified_at`, `user_id`)
VALUES (1, 'helloworld', 1708165162301, 1708165162301, 1);