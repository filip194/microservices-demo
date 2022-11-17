-- THIS SHOULD BE EXECUTED MANUALLY ON POSTGRES DB

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('0a41f156-fb78-4a19-86eb-dd35522288c2', 'app_user', 'App', 'User');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('830fa9a5-dfe8-41d6-8571-2a5d4d292909', 'app_admin', 'Admin', 'User');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('7769cef4-29fc-48e2-88ef-2f7db1b30005', 'app_super_user', 'Super', 'User');


INSERT INTO documents(id, document_id) VALUES ('c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 161276347979793260);
INSERT INTO documents(id, document_id) VALUES ('f2b2d644-3a08-4acb-ae07-20569f6f2a01', 2941738364592973348);
INSERT INTO documents(id, document_id) VALUES ('90573d2b-9a5d-409e-bbb6-b94189709a19', 7790448712558470456);


INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(),'0a41f156-fb78-4a19-86eb-dd35522288c2', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(),'830fa9a5-dfe8-41d6-8571-2a5d4d292909', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(),'830fa9a5-dfe8-41d6-8571-2a5d4d292909', 'f2b2d644-3a08-4acb-ae07-20569f6f2a01', 'READ');

INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(), '830fa9a5-dfe8-41d6-8571-2a5d4d292909', '90573d2b-9a5d-409e-bbb6-b94189709a19', 'READ');

INSERT INTO user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(), '7769cef4-29fc-48e2-88ef-2f7db1b30005', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');
