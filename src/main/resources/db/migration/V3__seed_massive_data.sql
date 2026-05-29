insert into categories (name, description) values
('Concerts', 'Live music and performance events'),
('Workshops', 'Hands-on learning sessions'),
('Conferences', 'Professional and academic conferences'),
('Sports', 'Competitive and recreational sports events'),
('Gastronomy', 'Food and beverage experiences'),
('Festivals', 'Large cultural and entertainment gatherings'),
('Theater', 'Stage plays and performing arts');

insert into venues (name, address, capacity, city) values
('Gran Teatro Central', 'Calle 10 # 5-20', 900, 'Bogotá'),
('Centro de Convenciones Norte', 'Carrera 45 # 80-10', 1200, 'Bogotá'),
('Auditorio Río', 'Avenida Regional # 30-50', 650, 'Medellín'),
('Plaza Cultural Sur', 'Calle 25 # 12-44', 500, 'Cali'),
('Arena Caribe', 'Vía 40 # 77-120', 1500, 'Barranquilla'),
('Casa Taller Andina', 'Calle 7 # 9-18', 180, 'Bucaramanga'),
('Foro Histórico', 'Centro Histórico # 2-15', 350, 'Cartagena'),
('Parque Deportivo Oriental', 'Avenida 6 # 22-90', 2000, 'Cúcuta'),
('Pabellón Gastronómico', 'Carrera 12 # 30-21', 450, 'Pereira'),
('Teatro Montaña', 'Calle 40 # 18-30', 700, 'Manizales');

insert into events (name, date, description, active, venue_id)
select
    case mod(x.n, 7)
        when 0 then 'Concierto de ROCK ' || x.n
        when 1 then 'Workshop de Innovación ' || x.n
        when 2 then 'Conferencia Tech ' || x.n
        when 3 then 'Torneo Deportivo ' || x.n
        when 4 then 'Ruta Gastronómica ' || x.n
        when 5 then 'Festival Cultural ' || x.n
        else 'Temporada de Teatro ' || x.n
    end,
    dateadd('day', x.n, date '2026-01-01'),
    'Evento semilla número ' || x.n,
    true,
    mod(x.n - 1, 10) + 1
from system_range(1, 200) x(n);

insert into events_categories (event_id, category_id)
select x.n, mod(x.n - 1, 7) + 1
from system_range(1, 200) x(n);

insert into events_categories (event_id, category_id)
select x.n, 6
from system_range(1, 200) x(n)
where mod(x.n, 10) = 0
  and mod(x.n - 1, 7) + 1 <> 6;
