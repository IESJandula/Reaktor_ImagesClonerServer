-- Esquema de referencia (desarrollo: spring.jpa.hibernate.ddl-auto=update)
-- Producción VPS: ddl-auto=validate — aplicar este script antes del despliegue.

CREATE TABLE IF NOT EXISTS imagen_clonezilla (
    nombre_imagen VARCHAR(255) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    accion VARCHAR(32) NOT NULL DEFAULT 'poweroff',
    PRIMARY KEY (nombre_imagen),
    CONSTRAINT chk_imagen_estado CHECK (estado IN ('DESACTIVADA', 'PENDIENTE', 'ACTIVADA')),
    CONSTRAINT chk_imagen_accion CHECK (accion IN ('poweroff', 'reboot', 'true'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
