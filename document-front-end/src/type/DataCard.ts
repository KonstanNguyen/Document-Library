export type DataCard = {
	id: number,
	categoryId: number,
    authorId: number,
	thumbnail: string;
    title: string;
    content: string;
    description: string;
    status: number;
    createAt: Date;  
    updateAt: Date; 
	views: number;
	ratings: number;
	href: string;
};
