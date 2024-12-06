export type DataCard = {
	id: number,
	category: {
        id: number,
        name: string,
    },
    author: {
        name: string;
    };
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
