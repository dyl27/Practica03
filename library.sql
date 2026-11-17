CREATE DATABASE IF NOT EXISTS library
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library;

DROP TABLE IF EXISTS books;

CREATE TABLE books (
  id     INT AUTO_INCREMENT PRIMARY KEY,
  name   VARCHAR(200)  NOT NULL,
  author VARCHAR(150)  NOT NULL,
  price  DECIMAL(10,2) NOT NULL CHECK (price >= 0)
);

INSERT INTO books (name, author, price) VALUES
('Cien años de soledad',                    'Gabriel García Márquez',   299.00),
('Crónica de una muerte anunciada',         'Gabriel García Márquez',   229.00),
('El amor en los tiempos del cólera',       'Gabriel García Márquez',   319.00),
('El coronel no tiene quien le escriba',    'Gabriel García Márquez',   189.00),
('Pedro Páramo',                            'Juan Rulfo',               189.50),
('El llano en llamas',                      'Juan Rulfo',               175.00),
('El laberinto de la soledad',              'Octavio Paz',              210.00),
('La muerte de Artemio Cruz',               'Carlos Fuentes',           265.00),
('Aura',                                    'Carlos Fuentes',           149.00),
('Rayuela',                                 'Julio Cortázar',           345.00),
('Bestiario',                               'Julio Cortázar',           199.00),
('Ficciones',                               'Jorge Luis Borges',        249.00),
('El Aleph',                                'Jorge Luis Borges',        239.00),
('La ciudad y los perros',                  'Mario Vargas Llosa',       329.00),
('La fiesta del chivo',                     'Mario Vargas Llosa',       359.00),
('La casa de los espíritus',                'Isabel Allende',           309.00),
('Como agua para chocolate',                'Laura Esquivel',           229.00),
('Los detectives salvajes',                 'Roberto Bolaño',           429.00),
('2666',                                    'Roberto Bolaño',           549.00),
('Balún Canán',                             'Rosario Castellanos',      219.00),
('Don Quijote de la Mancha',                'Miguel de Cervantes',      399.00),
('1984',                                    'George Orwell',            249.00),
('Rebelión en la granja',                   'George Orwell',            179.00),
('Un mundo feliz',                          'Aldous Huxley',            239.00),
('Fahrenheit 451',                          'Ray Bradbury',             219.00),
('Crimen y castigo',                        'Fiódor Dostoievski',       329.00),
('Los hermanos Karamázov',                  'Fiódor Dostoievski',       459.00),
('La metamorfosis',                         'Franz Kafka',              139.00),
('El proceso',                              'Franz Kafka',              209.00),
('Orgullo y prejuicio',                     'Jane Austen',              229.00),
('Frankenstein',                            'Mary Shelley',             199.00),
('El principito',                           'Antoine de Saint-Exupéry', 159.00),
('El extranjero',                           'Albert Camus',             199.00),
('Ensayo sobre la ceguera',                 'José Saramago',            299.00),
('Dune',                                    'Frank Herbert',            389.00),
('Fundación',                               'Isaac Asimov',             279.00),
('Yo, robot',                               'Isaac Asimov',             249.00),
('Introduction to Algorithms',              'Thomas H. Cormen',        1450.00),
('Effective Java',                          'Joshua Bloch',             980.00),
('Clean Code',                              'Robert C. Martin',         850.00),
('Clean Architecture',                      'Robert C. Martin',         890.00),
('Design Patterns',                         'Erich Gamma',             1120.00),
('Refactoring',                             'Martin Fowler',            990.00),
('Patterns of Enterprise Application Architecture', 'Martin Fowler',   1180.00),
('The Pragmatic Programmer',                'Andrew Hunt',              920.00),
('Spring in Action',                        'Craig Walls',              870.00),
('Head First Java',                         'Kathy Sierra',             760.00),
('Structure and Interpretation of Computer Programs', 'Harold Abelson', 1050.00),
('Computer Networking: A Top-Down Approach', 'James F. Kurose',        1290.00),
('Deep Learning',                           'Ian Goodfellow',          1390.00);
