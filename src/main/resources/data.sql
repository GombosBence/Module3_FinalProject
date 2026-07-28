INSERT INTO training_type (training_type_name)
SELECT 'FITNESS' WHERE NOT EXISTS (SELECT 1 FROM training_type WHERE training_type_name = 'FITNESS');

INSERT INTO training_type (training_type_name)
SELECT 'YOGA' WHERE NOT EXISTS (SELECT 1 FROM training_type WHERE training_type_name = 'YOGA');

INSERT INTO training_type (training_type_name)
SELECT 'ZUMBA' WHERE NOT EXISTS (SELECT 1 FROM training_type WHERE training_type_name = 'ZUMBA');

INSERT INTO training_type (training_type_name)
SELECT 'STRETCHING' WHERE NOT EXISTS (SELECT 1 FROM training_type WHERE training_type_name = 'STRETCHING');

INSERT INTO training_type (training_type_name)
SELECT 'RESISTANCE' WHERE NOT EXISTS (SELECT 1 FROM training_type WHERE training_type_name = 'RESISTANCE');