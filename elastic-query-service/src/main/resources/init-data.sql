CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('1cbdcf25-2e53-488b-a7da-8372f1aba921', 'app_user', 'Standard', 'User');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('6f131464-44fc-43ad-8375-9b472124e728', 'app_admin', 'Admin', 'User');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('b4d35dee-c131-4dc8-aaf3-2ed1280c4087', 'app_super_user', 'Super', 'User');


INSERT INTO documents(id, document_id) VALUES ('c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 161276347979793260);
INSERT INTO documents(id, document_id) VALUES ('f2b2d644-3a08-4acb-ae07-20569f6f2a01', 2941738364592973348);
INSERT INTO documents(id, document_id) VALUES ('90573d2b-9a5d-409e-bbb6-b94189709a19', 7790448712558470456);


INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(),'1cbdcf25-2e53-488b-a7da-8372f1aba921', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(),'6f131464-44fc-43ad-8375-9b472124e728', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(),'6f131464-44fc-43ad-8375-9b472124e728', 'f2b2d644-3a08-4acb-ae07-20569f6f2a01', 'READ');

INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(), '6f131464-44fc-43ad-8375-9b472124e728', '90573d2b-9a5d-409e-bbb6-b94189709a19', 'READ');

INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(), 'b4d35dee-c131-4dc8-aaf3-2ed1280c4087', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');
