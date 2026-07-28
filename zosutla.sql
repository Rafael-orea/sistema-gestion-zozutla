-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 29-07-2026 a las 01:17:30
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `zosutla`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alcancia`
--

CREATE TABLE `alcancia` (
  `id_alcancia` int(11) NOT NULL,
  `id_molde` int(11) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `existencia` int(11) NOT NULL DEFAULT 0,
  `precio` decimal(10,2) NOT NULL,
  `estado` enum('disponible','agotado') NOT NULL DEFAULT 'disponible'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `alcancia`
--

INSERT INTO `alcancia` (`id_alcancia`, `id_molde`, `nombre`, `descripcion`, `existencia`, `precio`, `estado`) VALUES
(1, 1, 'Alcancia Aguila Grande', 'Alcancia decorativa figura aguila grande', 94, 120.00, 'disponible'),
(2, 2, 'Alcancia Baby Yoda', 'Alcancia figura Baby Yoda grande', 50, 150.00, 'disponible'),
(3, 3, 'Alcancia Barril Jalisco', 'Alcancia en forma de barril estilo Jalisco', 78, 130.00, 'disponible'),
(4, 4, 'Alcancia Batman', 'Alcancia figura Batman', 0, 140.00, 'agotado'),
(5, 7, 'Alcancia Borracho', 'Alcancia figura Borracho', 10, 150.00, 'disponible');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `id_cliente` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `pais` varchar(60) NOT NULL,
  `tipo` enum('nacional','internacional') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`id_cliente`, `nombre`, `telefono`, `pais`, `tipo`) VALUES
(1, 'Juan Pérez', '5512345678', 'México', 'nacional'),
(2, 'Juan Perez', '2221234567', 'Mexico', 'nacional'),
(3, 'Maria Garcia', '2229876543', 'Mexico', 'nacional'),
(4, 'Carlos Lopez', '5551234567', 'Guatemala', 'internacional'),
(5, 'Ana Martinez', '5559876543', 'USA', 'internacional');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `costo_produccion`
--

CREATE TABLE `costo_produccion` (
  `id_costo` int(11) NOT NULL,
  `id_alcancia` int(11) NOT NULL,
  `mano_obra` decimal(10,2) NOT NULL DEFAULT 0.00,
  `gastos_indirectos` decimal(10,2) NOT NULL DEFAULT 0.00,
  `costo_total` decimal(10,2) NOT NULL DEFAULT 0.00,
  `precio_venta` decimal(10,2) NOT NULL DEFAULT 0.00,
  `ganancia` decimal(10,2) NOT NULL DEFAULT 0.00,
  `margen` decimal(5,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_alcancia_insumo`
--

CREATE TABLE `detalle_alcancia_insumo` (
  `id_alcancia` int(11) NOT NULL,
  `id_insumo` int(11) NOT NULL,
  `cantidad` decimal(10,3) NOT NULL,
  `costo_unitario` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `detalle_alcancia_insumo`
--

INSERT INTO `detalle_alcancia_insumo` (`id_alcancia`, `id_insumo`, `cantidad`, `costo_unitario`) VALUES
(3, 3, 1.000, 2.00),
(4, 3, 1.000, 2.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_proveedor_insumo`
--

CREATE TABLE `detalle_proveedor_insumo` (
  `id_detalle_prov` int(11) NOT NULL,
  `id_proveedor` int(11) NOT NULL,
  `id_insumo` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `precio` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_venta`
--

CREATE TABLE `detalle_venta` (
  `id_detalle` int(11) NOT NULL,
  `id_venta` int(11) NOT NULL,
  `id_alcancia` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `detalle_venta`
--

INSERT INTO `detalle_venta` (`id_detalle`, `id_venta`, `id_alcancia`, `cantidad`, `precio_unitario`, `subtotal`) VALUES
(1, 3, 1, 6, 120.00, 720.00),
(2, 4, 3, 1, 130.00, 130.00),
(3, 5, 3, 1, 130.00, 130.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleado`
--

CREATE TABLE `empleado` (
  `id_empleado` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `puesto` varchar(80) NOT NULL,
  `salario` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `envio`
--

CREATE TABLE `envio` (
  `id_envio` int(11) NOT NULL,
  `id_venta` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `destino` varchar(100) NOT NULL,
  `fecha` date NOT NULL,
  `estado` enum('en_proceso','entregado','con_incidencia') NOT NULL DEFAULT 'en_proceso'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `envio`
--

INSERT INTO `envio` (`id_envio`, `id_venta`, `id_usuario`, `destino`, `fecha`, `estado`) VALUES
(1, 4, 1, 'Mexico', '2026-07-01', 'entregado');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `incidencia_envio`
--

CREATE TABLE `incidencia_envio` (
  `id_incidencia` int(11) NOT NULL,
  `id_envio` int(11) NOT NULL,
  `faltantes` int(11) NOT NULL DEFAULT 0,
  `rotas` int(11) NOT NULL DEFAULT 0,
  `descripcion` text DEFAULT NULL,
  `fecha` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `incidencia_envio`
--

INSERT INTO `incidencia_envio` (`id_incidencia`, `id_envio`, `faltantes`, `rotas`, `descripcion`, `fecha`) VALUES
(1, 1, 1, 0, 'falto algo', '2026-07-27');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `insumo`
--

CREATE TABLE `insumo` (
  `id_insumo` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `unidad` varchar(30) NOT NULL,
  `cantidad` decimal(10,3) NOT NULL DEFAULT 0.000,
  `precio_unitario` decimal(10,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `insumo`
--

INSERT INTO `insumo` (`id_insumo`, `nombre`, `unidad`, `cantidad`, `precio_unitario`) VALUES
(1, 'YESO', 'kg', 100.000, 8.50),
(2, 'PINTURA', 'litros', 50.000, 45.00),
(3, 'AGUA', 'litros', 200.000, 2.00),
(4, 'PEGAMENTO', 'kg', 30.000, 35.00),
(5, 'PELAJE', 'metros', 20.000, 120.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `molde`
--

CREATE TABLE `molde` (
  `id_molde` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 0,
  `estado` enum('bueno','dañado','fuera_de_uso') NOT NULL DEFAULT 'bueno',
  `fecha_registro` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `molde`
--

INSERT INTO `molde` (`id_molde`, `id_usuario`, `nombre`, `cantidad`, `estado`, `fecha_registro`) VALUES
(1, 1, 'AGUILA GDE', 1, 'bueno', '2026-07-11'),
(2, 1, 'BABY YODA GDE', 2, 'bueno', '2026-07-11'),
(3, 1, 'BARRIL JALISCO', 1, 'bueno', '2026-07-11'),
(4, 1, 'BATMAN', 1, 'bueno', '2026-07-11'),
(5, 1, 'BLUE', 1, 'bueno', '2026-07-11'),
(6, 1, 'BOCHO GDE', 1, 'bueno', '2026-07-11'),
(7, 1, 'BORRACHO', 1, 'bueno', '2026-07-11'),
(8, 1, 'BORREGO CHINO CH', 2, 'bueno', '2026-07-11'),
(9, 1, 'BORREGO CIMARRON GDE', 1, 'bueno', '2026-07-11'),
(10, 1, 'BORREGO DORPER MED', 2, 'bueno', '2026-07-11'),
(11, 1, 'BOWSER MED', 1, 'bueno', '2026-07-11'),
(12, 1, 'BOWSER GDE', 1, 'bueno', '2026-07-11'),
(13, 1, 'BUFALO', 1, 'bueno', '2026-07-11'),
(14, 1, 'BUHO CHICO', 2, 'bueno', '2026-07-11'),
(15, 1, 'BUHO GRANDE', 1, 'bueno', '2026-07-11'),
(16, 1, 'BULLIN GDE', 2, 'bueno', '2026-07-11'),
(17, 1, 'BULLIN SENTADO', 1, 'bueno', '2026-07-11'),
(18, 1, 'BULTERRY GDE', 1, 'bueno', '2026-07-11'),
(19, 1, 'BURRO (A)', 1, 'bueno', '2026-07-11'),
(20, 1, 'BUZZ LIGHTYEAR', 1, 'bueno', '2026-07-11'),
(21, 1, 'CABALLO DE BASE', 2, 'bueno', '2026-07-11'),
(22, 1, 'CABALLO PERCHERON GDE', 2, 'bueno', '2026-07-11'),
(23, 1, 'CABALLO PERCHERON PATAS GORDAS', 1, 'bueno', '2026-07-11'),
(24, 1, 'CABEZA BATMAN', 1, 'bueno', '2026-07-11'),
(25, 1, 'CABEZA HOMBRE ARANA MED', 1, 'bueno', '2026-07-11'),
(26, 1, 'CAMIONETA GDE', 1, 'bueno', '2026-07-11'),
(27, 1, 'CAMIONETA CLASICA', 1, 'bueno', '2026-07-11'),
(28, 1, 'CANTINFLAS', 1, 'bueno', '2026-07-11'),
(29, 1, 'CAPIBARA MED', 1, 'bueno', '2026-07-11'),
(30, 1, 'CAPIBARA GDE', 1, 'bueno', '2026-07-11'),
(31, 1, 'CAPIBARA JUMBO', 1, 'bueno', '2026-07-11'),
(32, 1, 'CATARINA', 1, 'bueno', '2026-07-11'),
(33, 1, 'CHANGA', 1, 'bueno', '2026-07-11'),
(34, 1, 'CHANGO MARIHUANO', 1, 'bueno', '2026-07-11'),
(35, 1, 'CHIHUAHUA', 1, 'bueno', '2026-07-11'),
(36, 1, 'CHIVO GDE', 1, 'bueno', '2026-07-11'),
(37, 1, 'CHOKI GDE', 3, 'bueno', '2026-07-11'),
(38, 1, 'CUELLO LARGO GDE', 2, 'bueno', '2026-07-11'),
(39, 1, 'DOBERMAN', 2, 'bueno', '2026-07-11'),
(40, 1, 'ELEFANTE MED', 1, 'bueno', '2026-07-11'),
(41, 1, 'ELEFANTE GDE', 2, 'bueno', '2026-07-11'),
(42, 1, 'ELEFANTE CHINO', 1, 'bueno', '2026-07-11'),
(43, 1, 'FIONA', 1, 'bueno', '2026-07-11'),
(44, 1, 'FURIA', 1, 'bueno', '2026-07-11'),
(45, 1, 'GALLO MED', 1, 'bueno', '2026-07-11'),
(46, 1, 'GALLO GDE', 2, 'bueno', '2026-07-11'),
(47, 1, 'GATO CON BOTAS GDE', 1, 'bueno', '2026-07-11'),
(48, 1, 'GATO SOX', 1, 'bueno', '2026-07-11'),
(49, 1, 'GOKU GDE', 1, 'bueno', '2026-07-11'),
(50, 1, 'GOKU TORSO', 1, 'bueno', '2026-07-11'),
(51, 1, 'GORILA DE TRONCO', 1, 'bueno', '2026-07-11'),
(52, 1, 'GOTZILLA BEBE GDE', 1, 'bueno', '2026-07-11'),
(53, 1, 'GUACAMAYA MED', 2, 'bueno', '2026-07-11'),
(54, 1, 'GUACAMAYA GDE', 1, 'bueno', '2026-07-11'),
(55, 1, 'HARAGAN', 1, 'bueno', '2026-07-11'),
(56, 1, 'HELLO KITTY GDE', 1, 'bueno', '2026-07-11'),
(57, 1, 'HONGO', 1, 'bueno', '2026-07-11'),
(58, 1, 'HONGO TOAD GDE', 1, 'bueno', '2026-07-11'),
(59, 1, 'HUSKY MED', 1, 'bueno', '2026-07-11'),
(60, 1, 'HUSKY GDE', 1, 'bueno', '2026-07-11'),
(61, 1, 'HUSKY SENTADO', 1, 'bueno', '2026-07-11'),
(62, 1, 'IRON MAN', 2, 'bueno', '2026-07-11'),
(63, 1, 'JAGUAR DE PALO GDE', 2, 'bueno', '2026-07-11'),
(64, 1, 'JAGUAR GDE', 1, 'bueno', '2026-07-11'),
(65, 1, 'JOSHI GDE', 2, 'bueno', '2026-07-11'),
(66, 1, 'KING KONK', 1, 'bueno', '2026-07-11'),
(67, 1, 'KUN FU PANDA', 1, 'bueno', '2026-07-11'),
(68, 1, 'LEON MED', 2, 'bueno', '2026-07-11'),
(69, 1, 'LEON GDE', 1, 'bueno', '2026-07-11'),
(70, 1, 'LEON SENTADO', 1, 'bueno', '2026-07-11'),
(71, 1, 'MAPACHE', 1, 'bueno', '2026-07-11'),
(72, 1, 'MARIO BROS GDE', 3, 'bueno', '2026-07-11'),
(73, 1, 'MARIO CARRITO', 1, 'bueno', '2026-07-11'),
(74, 1, 'MARIO SORPRESA', 2, 'bueno', '2026-07-11'),
(75, 1, 'MARRANA GDE', 3, 'bueno', '2026-07-11'),
(76, 1, 'MILLONARIO GDE', 1, 'bueno', '2026-07-11'),
(77, 1, 'MINION GDE', 1, 'bueno', '2026-07-11'),
(78, 1, 'MINION SENTADO', 1, 'bueno', '2026-07-11'),
(79, 1, 'MINNIE MOUSE', 1, 'bueno', '2026-07-11'),
(80, 1, 'MUNECA MED', 2, 'bueno', '2026-07-11'),
(81, 1, 'MUNECA GDE', 4, 'bueno', '2026-07-11'),
(82, 1, 'PANDA', 2, 'bueno', '2026-07-11'),
(83, 1, 'PANTERA GDE', 1, 'bueno', '2026-07-11'),
(84, 1, 'PANTERA SENTADA', 1, 'bueno', '2026-07-11'),
(85, 1, 'PAYASO ESO', 1, 'bueno', '2026-07-11'),
(86, 1, 'PEACH', 2, 'bueno', '2026-07-11'),
(87, 1, 'PEDRO PICAPIEDRA', 1, 'bueno', '2026-07-11'),
(88, 1, 'PERRO BULLDOG INGLES', 1, 'bueno', '2026-07-11'),
(89, 1, 'PERRO DE BALON', 1, 'bueno', '2026-07-11'),
(90, 1, 'PERRO GALGO', 1, 'bueno', '2026-07-11'),
(91, 1, 'PERRO HUESITO', 1, 'bueno', '2026-07-11'),
(92, 1, 'PERRO LABRADOR', 1, 'bueno', '2026-07-11'),
(93, 1, 'PERRO OREJON', 1, 'bueno', '2026-07-11'),
(94, 1, 'PERRO PACHON', 1, 'bueno', '2026-07-11'),
(95, 1, 'PERRO YOGA', 1, 'bueno', '2026-07-11'),
(96, 1, 'PIPA', 1, 'bueno', '2026-07-11'),
(97, 1, 'PIRANA', 1, 'bueno', '2026-07-11'),
(98, 1, 'PIT BULL DE COLLAR', 1, 'bueno', '2026-07-11'),
(99, 1, 'PIT BULL GDE', 1, 'bueno', '2026-07-11'),
(100, 1, 'PROFE', 1, 'bueno', '2026-07-11'),
(101, 1, 'REX', 2, 'bueno', '2026-07-11'),
(102, 1, 'ROT SENTADO MED', 2, 'bueno', '2026-07-11'),
(103, 1, 'ROT SENTADO GDE', 2, 'bueno', '2026-07-11'),
(104, 1, 'ROTWAILER GDE', 1, 'bueno', '2026-07-11'),
(105, 1, 'SAN JUDAS GDE', 2, 'bueno', '2026-07-11'),
(106, 1, 'SAYAYIN', 1, 'bueno', '2026-07-11'),
(107, 1, 'SEMENTAL', 2, 'bueno', '2026-07-11'),
(108, 1, 'SHREK', 1, 'bueno', '2026-07-11'),
(109, 1, 'SIMPSON GDE', 1, 'bueno', '2026-07-11'),
(110, 1, 'SIRENA', 1, 'bueno', '2026-07-11'),
(111, 1, 'SNAHUSER', 2, 'bueno', '2026-07-11'),
(112, 1, 'SNOOPY GDE', 1, 'bueno', '2026-07-11'),
(113, 1, 'SONIC GDE', 3, 'bueno', '2026-07-11'),
(114, 1, 'SONIC GIRL', 1, 'bueno', '2026-07-11'),
(115, 1, 'SONIC MALO', 1, 'bueno', '2026-07-11'),
(116, 1, 'SONIC NEGRO', 1, 'bueno', '2026-07-11'),
(117, 1, 'SPIDER MAN', 1, 'bueno', '2026-07-11'),
(118, 1, 'STICH GDE', 1, 'bueno', '2026-07-11'),
(119, 1, 'STICH BASE', 1, 'bueno', '2026-07-11'),
(120, 1, 'SR PATO', 1, 'bueno', '2026-07-11'),
(121, 1, 'SUPER MAN', 1, 'bueno', '2026-07-11'),
(122, 1, 'TAINLUG', 1, 'bueno', '2026-07-11'),
(123, 1, 'TANQUE GAS GDE', 1, 'bueno', '2026-07-11'),
(124, 1, 'TARRO DE CERVEZA CHICO', 1, 'bueno', '2026-07-11'),
(125, 1, 'TARRO DE CERVEZA EMPELUCHADO', 1, 'bueno', '2026-07-11'),
(126, 1, 'TAZ', 1, 'bueno', '2026-07-11'),
(127, 1, 'TIGRE MED', 1, 'bueno', '2026-07-11'),
(128, 1, 'TIGRE GDE', 1, 'bueno', '2026-07-11'),
(129, 1, 'TIGREZA', 1, 'bueno', '2026-07-11'),
(130, 1, 'TIFANY', 1, 'bueno', '2026-07-11'),
(131, 1, 'TORO CEBU MED', 1, 'bueno', '2026-07-11'),
(132, 1, 'TORO CEBU GDE', 1, 'bueno', '2026-07-11'),
(133, 1, 'TORO CEBU SIN CUERNOS', 1, 'bueno', '2026-07-11'),
(134, 1, 'TORO DE LIDIA MED', 1, 'bueno', '2026-07-11'),
(135, 1, 'TORO DE LIDIA GDE', 2, 'bueno', '2026-07-11'),
(136, 1, 'TORO ZARDO', 2, 'bueno', '2026-07-11'),
(137, 1, 'TRAILER', 1, 'bueno', '2026-07-11'),
(138, 1, 'TRANSFORMER AMARILLO', 1, 'bueno', '2026-07-11'),
(139, 1, 'TRANSFORMER AZUL', 1, 'bueno', '2026-07-11'),
(140, 1, 'TRONCOMOVIL', 1, 'bueno', '2026-07-11'),
(141, 1, 'UNICORNIO GDE', 2, 'bueno', '2026-07-11'),
(142, 1, 'VACA GDE', 2, 'bueno', '2026-07-11'),
(143, 1, 'VACA LECHERA', 1, 'bueno', '2026-07-11'),
(144, 1, 'VIRGEN MED', 0, 'bueno', '2026-07-11'),
(145, 1, 'VIRGEN GDE', 1, 'bueno', '2026-07-11'),
(147, 1, 'CATRINA', 2, 'dañado', '2026-07-11');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proveedor`
--

CREATE TABLE `proveedor` (
  `id_proveedor` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `pais` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reporte`
--

CREATE TABLE `reporte` (
  `id_reporte` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `tipo` enum('produccion','ventas','rentabilidad','moldes','envios') NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_final` date NOT NULL,
  `formato` enum('pdf','excel') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `rol` enum('administrador','operador') NOT NULL DEFAULT 'operador'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `usuario`, `contrasena`, `nombre`, `rol`) VALUES
(1, 'admin', '1234', 'Administrador Zozutla', 'administrador');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `venta`
--

CREATE TABLE `venta` (
  `id_venta` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_cliente` int(11) NOT NULL,
  `folio` varchar(20) NOT NULL,
  `fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `total` decimal(10,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `venta`
--

INSERT INTO `venta` (`id_venta`, `id_usuario`, `id_cliente`, `folio`, `fecha`, `total`) VALUES
(3, 1, 1, 'VTA-1784251303252', '2026-07-16 00:00:00', 720.00),
(4, 1, 2, 'VTA-1784927410230', '2026-07-24 00:00:00', 130.00),
(5, 1, 3, 'VTA-1784927443279', '2026-07-24 00:00:00', 130.00);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `alcancia`
--
ALTER TABLE `alcancia`
  ADD PRIMARY KEY (`id_alcancia`),
  ADD KEY `id_molde` (`id_molde`);

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id_cliente`);

--
-- Indices de la tabla `costo_produccion`
--
ALTER TABLE `costo_produccion`
  ADD PRIMARY KEY (`id_costo`),
  ADD UNIQUE KEY `id_alcancia` (`id_alcancia`);

--
-- Indices de la tabla `detalle_alcancia_insumo`
--
ALTER TABLE `detalle_alcancia_insumo`
  ADD PRIMARY KEY (`id_alcancia`,`id_insumo`),
  ADD KEY `id_insumo` (`id_insumo`);

--
-- Indices de la tabla `detalle_proveedor_insumo`
--
ALTER TABLE `detalle_proveedor_insumo`
  ADD PRIMARY KEY (`id_detalle_prov`),
  ADD KEY `id_proveedor` (`id_proveedor`),
  ADD KEY `id_insumo` (`id_insumo`);

--
-- Indices de la tabla `detalle_venta`
--
ALTER TABLE `detalle_venta`
  ADD PRIMARY KEY (`id_detalle`),
  ADD KEY `id_venta` (`id_venta`),
  ADD KEY `id_alcancia` (`id_alcancia`);

--
-- Indices de la tabla `empleado`
--
ALTER TABLE `empleado`
  ADD PRIMARY KEY (`id_empleado`);

--
-- Indices de la tabla `envio`
--
ALTER TABLE `envio`
  ADD PRIMARY KEY (`id_envio`),
  ADD KEY `id_venta` (`id_venta`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `incidencia_envio`
--
ALTER TABLE `incidencia_envio`
  ADD PRIMARY KEY (`id_incidencia`),
  ADD KEY `id_envio` (`id_envio`);

--
-- Indices de la tabla `insumo`
--
ALTER TABLE `insumo`
  ADD PRIMARY KEY (`id_insumo`);

--
-- Indices de la tabla `molde`
--
ALTER TABLE `molde`
  ADD PRIMARY KEY (`id_molde`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `proveedor`
--
ALTER TABLE `proveedor`
  ADD PRIMARY KEY (`id_proveedor`);

--
-- Indices de la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD PRIMARY KEY (`id_reporte`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `usuario` (`usuario`);

--
-- Indices de la tabla `venta`
--
ALTER TABLE `venta`
  ADD PRIMARY KEY (`id_venta`),
  ADD UNIQUE KEY `folio` (`folio`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_cliente` (`id_cliente`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `alcancia`
--
ALTER TABLE `alcancia`
  MODIFY `id_alcancia` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `id_cliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `costo_produccion`
--
ALTER TABLE `costo_produccion`
  MODIFY `id_costo` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `detalle_proveedor_insumo`
--
ALTER TABLE `detalle_proveedor_insumo`
  MODIFY `id_detalle_prov` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `detalle_venta`
--
ALTER TABLE `detalle_venta`
  MODIFY `id_detalle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `empleado`
--
ALTER TABLE `empleado`
  MODIFY `id_empleado` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `envio`
--
ALTER TABLE `envio`
  MODIFY `id_envio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `incidencia_envio`
--
ALTER TABLE `incidencia_envio`
  MODIFY `id_incidencia` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `insumo`
--
ALTER TABLE `insumo`
  MODIFY `id_insumo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `molde`
--
ALTER TABLE `molde`
  MODIFY `id_molde` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=149;

--
-- AUTO_INCREMENT de la tabla `proveedor`
--
ALTER TABLE `proveedor`
  MODIFY `id_proveedor` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `reporte`
--
ALTER TABLE `reporte`
  MODIFY `id_reporte` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `venta`
--
ALTER TABLE `venta`
  MODIFY `id_venta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `alcancia`
--
ALTER TABLE `alcancia`
  ADD CONSTRAINT `alcancia_ibfk_1` FOREIGN KEY (`id_molde`) REFERENCES `molde` (`id_molde`);

--
-- Filtros para la tabla `costo_produccion`
--
ALTER TABLE `costo_produccion`
  ADD CONSTRAINT `costo_produccion_ibfk_1` FOREIGN KEY (`id_alcancia`) REFERENCES `alcancia` (`id_alcancia`);

--
-- Filtros para la tabla `detalle_alcancia_insumo`
--
ALTER TABLE `detalle_alcancia_insumo`
  ADD CONSTRAINT `detalle_alcancia_insumo_ibfk_1` FOREIGN KEY (`id_alcancia`) REFERENCES `alcancia` (`id_alcancia`),
  ADD CONSTRAINT `detalle_alcancia_insumo_ibfk_2` FOREIGN KEY (`id_insumo`) REFERENCES `insumo` (`id_insumo`);

--
-- Filtros para la tabla `detalle_proveedor_insumo`
--
ALTER TABLE `detalle_proveedor_insumo`
  ADD CONSTRAINT `detalle_proveedor_insumo_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`),
  ADD CONSTRAINT `detalle_proveedor_insumo_ibfk_2` FOREIGN KEY (`id_insumo`) REFERENCES `insumo` (`id_insumo`);

--
-- Filtros para la tabla `detalle_venta`
--
ALTER TABLE `detalle_venta`
  ADD CONSTRAINT `detalle_venta_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`),
  ADD CONSTRAINT `detalle_venta_ibfk_2` FOREIGN KEY (`id_alcancia`) REFERENCES `alcancia` (`id_alcancia`);

--
-- Filtros para la tabla `envio`
--
ALTER TABLE `envio`
  ADD CONSTRAINT `envio_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`),
  ADD CONSTRAINT `envio_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `incidencia_envio`
--
ALTER TABLE `incidencia_envio`
  ADD CONSTRAINT `incidencia_envio_ibfk_1` FOREIGN KEY (`id_envio`) REFERENCES `envio` (`id_envio`);

--
-- Filtros para la tabla `molde`
--
ALTER TABLE `molde`
  ADD CONSTRAINT `molde_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD CONSTRAINT `reporte_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `venta`
--
ALTER TABLE `venta`
  ADD CONSTRAINT `venta_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `venta_ibfk_2` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
