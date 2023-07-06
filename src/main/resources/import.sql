INSERT INTO `category` (`name`) VALUES ('paesaggi');
INSERT INTO `category` (`name`) VALUES ('ritratti');
INSERT INTO `category` (`name`) VALUES ('street');
INSERT INTO `category` (`name`) VALUES ('natura');
INSERT INTO `foto` (`id`, `description`, `picture_url`, `title`, `visible`) VALUES (NULL, 'Ritratto di un ragazzo affascinante.', 'https://picsum.photos/200/300?random=1', 'Ragazzo in blu', true);
INSERT INTO `foto` (`id`, `description`, `picture_url`, `title`, `visible`) VALUES (NULL, 'Albero in collina', 'https://picsum.photos/200/300?random=2', 'Albero in collina nel verde con saturazione minima', false);
INSERT INTO `foto` (`id`, `description`, `picture_url`, `title`, `visible`) VALUES (NULL, 'Ritratto di donna anziana.', 'https://picsum.photos/200/300?random=3', 'Donna anziana in vestaglia', true);
INSERT INTO `foto` (`id`, `description`, `picture_url`, `title`, `visible`) VALUES (NULL, 'Via del Corso a Roma', 'https://picsum.photos/200/300?random=4', 'Passeggiando per via del Corso', true);

INSERT INTO `foto_categories` (`fotos_id`, `categories_id`) VALUES ('1', '2');
INSERT INTO `foto_categories` (`fotos_id`, `categories_id`) VALUES ('2', '4');
INSERT INTO `foto_categories` (`fotos_id`, `categories_id`) VALUES ('3', '2');
INSERT INTO `foto_categories` (`fotos_id`, `categories_id`) VALUES ('4', '3');
