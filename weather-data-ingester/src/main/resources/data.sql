INSERT INTO sensors (id, name) VALUES
    (0, 'Sherkin Island Station'),
    (1, 'Mace Head Station'),
    (2, 'Malin Head Station')
ON CONFLICT (id) DO NOTHING;