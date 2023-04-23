--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: user
--

INSERT INTO public.products (p, productid, price, type, version, size, length, weight) VALUES ('Ski','ae8c5861-3c46-44d4-a090-115d4926395f', 980.125, 'SKI', 0, NULL, 12, 12);
INSERT INTO public.products (p, productid, price, type, version, size, length, weight) VALUES ('Ski','5470ec59-01f6-426c-b7fd-2dd11212bb11', 180, 'SKI', 0, NULL, 12, 13);
INSERT INTO public.products (p, productid, price, type, version, size, length, weight) VALUES ('SkiBoot','b86866d7-6210-4d69-afa4-b564594c711a', 80.99, 'SKIBOOT', 0, 45, NULL, NULL);
INSERT INTO public.products (p, productid, price, type, version, size, length, weight) VALUES ('SkiBoot','255a4740-b310-47d0-9a23-7ba07f338590', 99.99, 'SKIBOOT', 0, 37, NULL, NULL);
INSERT INTO public.products (p, productid, price, type, version, size, length, weight) VALUES ('Ski','c10217b3-b723-4212-b978-c222a8b4f29b', 12.75, 'SKI', 0, NULL, 173, 20);
INSERT INTO public.products (p, productid, price, type, version, size, length, weight) VALUES ('Ski','cf1e58ba-fc4f-4251-85cd-f3d7819a3659', 12.75, 'SKI', 0, NULL, 173, 20);

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: user
--

INSERT INTO public.clients (id, active) VALUES ('02171fb3-d462-4bd7-9bd0-4ca635e5c9fb', false);
INSERT INTO public.clients (id, active) VALUES ('0a6d766f-473b-46e1-b6d6-1571446465f6', true);
INSERT INTO public.clients (id, active) VALUES ('59e2472c-1137-4646-9928-fc21d4790d40', true);
INSERT INTO public.clients (id, active) VALUES ('4c6319c6-2530-403e-ba1c-7d0e8e505e78', true);
INSERT INTO public.clients (id, active) VALUES ('86a3b048-45b4-4de9-8b42-855d4fa8b0c4', true);


--
-- Data for Name: reservations; Type: TABLE DATA; Schema: public; Owner: user
--

INSERT INTO public.reservations (reservationid, end_date, start_date, version, id, productid) VALUES ('5185cda6-617d-4f1c-a43a-bed4c96a0232', '2023-02-20', '2023-02-12', 0, '59e2472c-1137-4646-9928-fc21d4790d40', 'ae8c5861-3c46-44d4-a090-115d4926395f');
INSERT INTO public.reservations (reservationid, end_date, start_date, version, id, productid) VALUES ('a134bee4-cf8a-4322-a017-3f05194d066b', '2023-10-17', '2023-03-09', 0, '59e2472c-1137-4646-9928-fc21d4790d40', 'ae8c5861-3c46-44d4-a090-115d4926395f');
INSERT INTO public.reservations (reservationid, end_date, start_date, version, id, productid) VALUES ('89fdca80-8b20-43db-8e74-a7cffebcac9f', '2023-07-21', '2023-07-11', 0, '59e2472c-1137-4646-9928-fc21d4790d40', '5470ec59-01f6-426c-b7fd-2dd11212bb11');
INSERT INTO public.reservations (reservationid, end_date, start_date, version, id, productid) VALUES ('b9598e58-1a9f-4bd1-877f-3c7e442aa794', '2023-08-09', '2023-08-01', 0, '59e2472c-1137-4646-9928-fc21d4790d40', '5470ec59-01f6-426c-b7fd-2dd11212bb11');


--
-- PostgreSQL database dump complete
--

