create table jobs(
  id uuid primary key default gen_random_uuid(),
  company text not null,
  title text not null,
  description text not null,
  externalUrl text not null,
  salaryLo integer,
  salaryHi integer,
  crrency text,
  remote boolean,
  location text not null,
  country text
);