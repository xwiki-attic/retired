
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Initial set up
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{style}[2004/03/24 2004 CleanBook LaTeX class]
\LoadClass{book}
\PassOptionsToClass{a4paper,16pt}{book}
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{book}}
\ProcessOptions

%% Packages
\RequirePackage{color}
\RequirePackage{colortbl}
\usepackage{scrpage2}
\usepackage{color}
\usepackage{colortbl}
\usepackage{typearea}
\usepackage{mathrsfs} 
% Needed by IDiP
\usepackage{multirow}
\usepackage{threeparttable}
\usepackage{comment}

%
% environments
%
\newtheorem{example}{Example}
\newtheorem{proposition}{Proposition}
\newtheorem{theorem}{Theorem}
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Page Layout
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%\setlength{\textwidth}{12cm}
% \setlength{\footskip}{2cm}
\setlength{\textheight}{24cm}
%\setlength{\oddsidemargin}{4.5cm}
\setlength{\topmargin}{-1.0cm}
\reversemarginpar
\setlength{\marginparwidth}{4cm}
\renewcommand{\arraystretch}{1.0}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Concerto colors
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
\definecolor{style@lightblue}{RGB}{1,94,140}
\definecolor{style@green}{RGB}{0,134,203}
\definecolor{style@red}{RGB}{189,16,11}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Headers and footers
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
\pagestyle{scrheadings}
\clearscrheadings
\clearscrplain

\setheadsepline{.4pt}[\color{black}] 
\setfootsepline{.4pt}[\color{black}] 
%\setheadwidth[-4.5cm]{16.5cm}
%\setfootwidth[-4.5cm]{16.5cm}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Redefinitions (colored headings)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% Paragraph skipping and indenting
\parskip 1.mm
\parindent 7.mm

\makeatletter
\renewcommand%
{\section}{\@startsection{section}%
{1}%
{0cm}%
{2\baselineskip}%
{1\baselineskip}%
{\Large\bf\textcolor{black}}%
}

\renewcommand%
{\subsection}{\@startsection{subsection}%
{1}%
{0cm}%
{2\baselineskip}%
{1\baselineskip}%
{\large\bf\textcolor{black}}%
}

\renewcommand%
{\subsubsection}{\@startsection{subsubsection}%
{1}%
{0cm}%
{2\baselineskip}%
{1\baselineskip}%
{\large\textcolor{black}}%
}
\makeatother
\newcommand{\autor}[1]{{\Large \bf \sf #1}}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Redefinitions (toc)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\renewcommand{\contentsname}{Contents}
\makeatletter
\renewcommand*\@dotsep{1}
\renewcommand*\l@section{\@dottedtocline{1}{1.5em} {2.3em}}
\renewcommand*\l@subsection{\@dottedtocline{2}{3.8 em}{3.2em}}
\renewcommand*\l@subsubsection{\@dottedtocline{3}{ 7.0em}{4.1em}}
\renewcommand*\l@paragraph{\@dottedtocline{4}{10em }{5em}}
\renewcommand*\l@subparagraph{\@dottedtocline{5}{1 2em}{6em}}
\renewcommand*\l@figure{\@dottedtocline{1}{1.5em}{ 2.3em}}
\renewcommand*\l@table{\@dottedtocline{1}{1.5em}{ 2.3em}}

\makeatother 




%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Redefinitions (misc)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
\newcommand{\marginlabel}[1]
	{\mbox{}\marginpar{\raggedright\hspace{0pt}#1}}
\renewcommand{\labelitemii}{$\circ$}


\usepackage[pdftex]{graphicx}


\usepackage{pdfpages}
\usepackage{url}
\usepackage{alltt}
\usepackage{subfigure}
% Use the times package but leave the standard tt font
\usepackage{times}
\renewcommand{\ttdefault}{cmtt}


